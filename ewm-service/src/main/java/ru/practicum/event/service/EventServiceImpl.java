package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.constant.AdminStateAction;
import ru.practicum.constant.StateEvent;
import ru.practicum.constant.StatusRequest;
import ru.practicum.constant.StatusRequestUpdate;
import ru.practicum.constant.UserStateAction;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEvent;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.PredicateParam;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ValidDateException;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.constant.Constant.YYYY_MM_DD_HH_MM_SS;
import static ru.practicum.transfer.PageSort.getPageable;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    private static final Integer TWO_HOURS_BEFORE_EVENT = 2;
    private static final Integer ONE_HOURS_BEFORE_EVENT = 1;


    @Override
    public Collection<EventShortDto> findEventsByOwner(long ownerId, Integer from, Integer size) {
        Pageable pageable = getPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        Iterable<Event> events = eventRepository.findAll(QEvent.event.initiator.id.eq(ownerId), pageable);
        Collection<Event> result = new ArrayList<>();
        events.forEach(result::add);
        return result.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();
        checkEventDate(newEventDto.getEventDate(), TWO_HOURS_BEFORE_EVENT);
        Event event = EventMapper.toEvent(newEventDto, category, initiator);
        EventFullDto eventFullDto;
        try {
            eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new ValidDateException(e.getMessage());
        }
        log.info("Event with id = '{}' and title = '{}' successful created",
                eventFullDto.getId(), eventFullDto.getTitle());
        return eventFullDto;
    }

    @Override
    public EventFullDto findEventByOwner(long ownerId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (event.getInitiator().getId() != ownerId)
            throw new InvalidParameterException(String.format("Not found events for user with id '%d'", ownerId));
        log.info("Found event with id '{}'", event.getId());
        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateByUser(long ownerId, long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ConflictException(String.format("Event with id '%d' has no available for update", eventId));
        }
        if (event.getInitiator().getId() != ownerId)
            throw new ConflictException(String.format("User with id '%d' has no available event", ownerId));
        if (request.getEventDate() != null) {
            checkEventDate(request.getEventDate(), TWO_HOURS_BEFORE_EVENT);
            event.setEventDate(request.getEventDate());
        }

        UserStateAction stateAction = request.getStateAction();
        if (stateAction != null) {
            if (stateAction.equals(UserStateAction.CANCEL_REVIEW)) {
                event.setState(StateEvent.CANCELED);
            } else if (stateAction.equals(UserStateAction.SEND_TO_REVIEW)) event.setState(StateEvent.PENDING);
        }

        Event updatedEvent = eventRepository.save(updateEvent(event, request));
        log.info("Event with id '{}' update", updatedEvent.getId());
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(long eventId, UpdateEventAdminRequest request) {
        if (request.getEventDate() != null) checkEventDate(request.getEventDate(), ONE_HOURS_BEFORE_EVENT);
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!event.getState().equals(StateEvent.PENDING))
            throw new ConflictException(String.format("Event can't update: current state '%s'", event.getState()));
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(AdminStateAction.REJECT_EVENT)
                    && event.getState().equals(StateEvent.PUBLISHED))
                throw new ConflictException(String.format("Event can't reject: current state '%S'", event.getState()));
            if (request.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                event.setState(StateEvent.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (request.getStateAction().equals(AdminStateAction.REJECT_EVENT))
                event.setState(StateEvent.CANCELED);
        }

        Event editEvent = eventRepository.saveAndFlush(updateEvent(event, request));
        log.info("Event with id '{}' update", editEvent.getId());
        return EventMapper.toEventFullDto(editEvent);
    }

    @Override
    public Collection<ParticipationRequestDto> findRequestsForEvent(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (event.getInitiator().getId() != userId)
            throw new ConflictException(String.format("Not found Event for User with id '%d'", userId));
        Collection<Request> requests = requestRepository.findAllByEventId(eventId);
        log.info("Found '{}' requests", requests.size());
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(long userId, long eventId,
                                                         EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (event.getInitiator().getId() != userId)
            throw new ConflictException(String.format("Not found Event for User with id '%d'", userId));
        List<Request> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        // если новый статус для заявок на участие в событии REJECTED
        if (request.getStatus().equals(StatusRequestUpdate.REJECTED)) {
            // для каждой заявки обновляем статус на REJECTED
            requests.forEach(req -> updateRequestStatus(req, StatusRequest.REJECTED, rejectedRequests));
            // иначе если новый статус для заявок на участие в событии CONFIRMED
        } else if (request.getStatus().equals(StatusRequestUpdate.CONFIRMED)) {
            // если для события лимит заявок равен 0 или отключена пре-модерация заявок
            if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
                // для каждой заявки обновляем статус на CONFIRMED
                requests.forEach(req -> updateRequestStatus(req, StatusRequest.CONFIRMED, confirmedRequests));
                // инкрементируем у события число подтвержденных заявок
                event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
            } else {
                for (Request req : requests) {
                    // если для события лимит заявок не равен 0 и лимит заявок исчерпан
                    if (event.getParticipantLimit() != 0
                            && event.getConfirmedRequests() == event.getParticipantLimit()) {
                        // заявку нужно отменить, добавить в список отклоненных и сохранить в базе данных
                        updateRequestStatus(req, StatusRequest.REJECTED, rejectedRequests);
                    } else {
                        // иначе подтверждаем заявку, вносим в список подтвержденных, сохраняем ее в репозиторий
                        updateRequestStatus(req, StatusRequest.CONFIRMED, confirmedRequests);
                        // и инкрементируем у события число подтвержденных заявок
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    }
                }
                // если был исчерпан лимит заявок и список не пустой, кидаем исключение
                if (!rejectedRequests.isEmpty())
                    throw new ConflictException(String.format(
                            "The limit on applications for event with Id '%d' has been reached", eventId));
            }
        }
        requestRepository.saveAll(requests);
        eventRepository.save(event);
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public Collection<EventShortDto> findAllByParamForPublic(PredicateParam param,
                                                             Integer from, Integer size, HttpServletRequest request) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (param.getSort() != null) {
            switch (param.getSort().toUpperCase()) {
                case "EVENT_DATE":
                    sort = Sort.by(Sort.Direction.DESC, "eventDate");
                    break;
                case "VIEWS":
                    sort = Sort.by(Sort.Direction.DESC, "views");
                    break;
                default:
                    throw new InvalidParameterException(String.format(
                            "For param 'sort' bad request '%s', Available values : EVENT_DATE, VIEWS",
                            param.getSort().toString()));
            }
        }
        Pageable pageable = getPageable(from, size, sort);
        Iterable<Event> events = eventRepository.findAll(buildQuery(param), pageable);
        List<Long> eventIds = new ArrayList<>();
        Collection<Event> result = new ArrayList<>();
        events.forEach(event -> {
            result.add(event);
            eventIds.add(event.getId());
        });
        List<String> uriList = new ArrayList<>();
        for (Long eventId : eventIds) {
            uriList.add("/events/" + eventId);
        }
        String start = param.getRangeStart().format(formatter);
        String end = param.getRangeEnd().format(formatter);
        String[] uris = uriList.toArray(new String[]{});
        Collection<ViewStatsDto> views = (Collection<ViewStatsDto>) statsClient.findStatsOfPeriod(
                start, end, uris, false).getBody();
        if (views.size() != 0) {
            for (ViewStatsDto view : views) {
                String s = view.getUri();
                String[] uri = view.getUri().split("/");
                Long id = Long.valueOf(uri[2]);
                result.stream().filter(event -> event.getId() == id).findFirst().get().setViews(view.getHits());
            }
        }
        return result.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public Collection<EventFullDto> findAllByParamForAdmin(PredicateParam param,
                                                           Integer from, Integer size) {
        Pageable pageable = getPageable(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Iterable<Event> events = eventRepository.findAll(buildQuery(param), pageable);
        Collection<Event> result = new ArrayList<>();
        events.forEach(result::add);
        return result.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    private Predicate buildQuery(PredicateParam param) {
        if (param.getRangeEnd() != null && param.getRangeStart() != null
                && param.getRangeStart().isAfter(param.getRangeEnd()))
            throw new ValidDateException("RangeStart can't be before RangeEnd");

        BooleanBuilder builder = new BooleanBuilder();
        if (param.getText() != null) builder.and(QEvent.event.annotation.containsIgnoreCase(param.getText())
                .or(QEvent.event.description.containsIgnoreCase(param.getText())));
        if (param.getPaid() != null) builder.and(QEvent.event.paid.eq(param.getPaid()));
        if (param.getUsers() != null) builder.and(QEvent.event.initiator.id.in(param.getUsers()));
        if (param.getStates() != null) builder.and(QEvent.event.state.in(param.getStates()));
        if (param.getCategories() != null) builder.and(QEvent.event.category.id.in(param.getCategories()));
        if (param.getRangeStart() == null) builder.and(QEvent.event.eventDate
                .after(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)));
        else builder.and(QEvent.event.eventDate.after(param.getRangeStart()));
        if (param.getRangeEnd() != null) builder.and(QEvent.event.eventDate.before(param.getRangeEnd()));
        if (param.getOnlyAvailable() != null && param.getOnlyAvailable())
            builder.and(QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit));
        if (param.getState() != null) builder.and(QEvent.event.state.eq(StateEvent.PUBLISHED));
        return builder.getValue();
    }


    @Override
    public EventFullDto findById(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!event.getState().equals(StateEvent.PUBLISHED))
            throw new InvalidParameterException(String.format("Event with id '%d' is not available", eventId));
        String start = event.getPublishedOn().format(formatter);
        String end = LocalDateTime.now().format(formatter);
        String[] uris = new String[]{request.getRequestURI()};
        Collection<ViewStatsDto> views = (Collection<ViewStatsDto>) statsClient.findStatsOfPeriod(
                start, end, uris, false).getBody();
        if (views != null)
            event.setViews(views.size());
        else event.setViews(0);
        return EventMapper.toEventFullDto(event);
    }


    private void checkRequestStatus(Request request) {
        if (!request.getStatus().equals(StatusRequest.PENDING))
            throw new ConflictException(String.format("Request id '%d' can't update, because request has status '%s'",
                    request.getId(), request.getStatus().toString()));
    }

    private void updateRequestStatus(Request req, StatusRequest status, List<ParticipationRequestDto> requestDtoList) {
        // статус можно изменить только у заявок, находящихся в состоянии ожидания
        checkRequestStatus(req);
        // меняем статус у заявки, добавляем в список  и сохраняем в базе данных
        req.setStatus(status);
        requestDtoList.add(RequestMapper.toParticipationRequestDto(req));
        requestRepository.save(req);
    }

    private void checkEventDate(LocalDateTime dateTime, long hours) {
        if (dateTime.minusHours(hours).isBefore(LocalDateTime.now()))
            throw new ValidDateException(
                    String.format("eventDate can't be earlier than '%d' hours before current moment", hours));
    }

    private Event updateEvent(Event event, NewEvent newEvent) {
        if (newEvent.getCategory() != null) {
            Category category = categoryRepository.findById(newEvent.getCategory()).orElseThrow();
            event.setCategory(category);
        }
        if (newEvent.getAnnotation() != null) event.setAnnotation(newEvent.getAnnotation());
        if (newEvent.getDescription() != null) event.setDescription(newEvent.getDescription());
        if (newEvent.getLocation() != null) event.setLocation(newEvent.getLocation());
        if (newEvent.getPaid() != null) event.setPaid(newEvent.getPaid());
        if (newEvent.getParticipantLimit() != null && newEvent.getParticipantLimit() != 0)
            event.setParticipantLimit(newEvent.getParticipantLimit());
        if (newEvent.getRequestModeration() != null) event.setRequestModeration(newEvent.getRequestModeration());
        if (newEvent.getTitle() != null) event.setTitle(newEvent.getTitle());
        return event;
    }
}
