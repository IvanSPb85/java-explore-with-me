package ru.practicum.privateApi.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adminApi.category.dao.CategoryRepository;
import ru.practicum.adminApi.category.model.Category;
import ru.practicum.adminApi.user.dao.UserRepository;
import ru.practicum.adminApi.user.model.User;
import ru.practicum.constant.AdminStateAction;
import ru.practicum.constant.State;
import ru.practicum.constant.UserStateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataBaseException;
import ru.practicum.privateApi.event.dao.EventRepository;
import ru.practicum.privateApi.event.dto.EventFullDto;
import ru.practicum.privateApi.event.dto.EventMapper;
import ru.practicum.privateApi.event.dto.EventShortDto;
import ru.practicum.privateApi.event.dto.NewEvent;
import ru.practicum.privateApi.event.dto.NewEventDto;
import ru.practicum.privateApi.event.dto.UpdateEventAdminRequest;
import ru.practicum.privateApi.event.dto.UpdateEventUserRequest;
import ru.practicum.privateApi.event.model.Event;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Collection<EventShortDto> findEventsByOwner(long ownerId, Integer from, Integer size) {
        if (from > 0 && size > 0) from = from / size;
        Collection<Event> events = eventRepository.findAllByOwner(ownerId, PageRequest.of(from, size));
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto)
            throws InvalidParameterException, NoSuchElementException {
        User initiator = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();
        checkEventDate(newEventDto.getEventDate(), 2);
        Event event = EventMapper.toEvent(newEventDto, category, initiator);
        EventFullDto eventFullDto;
        try {
            eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException(e.getMessage());
        }
        log.info("Event with id = {} and title = {} successful created",
                eventFullDto.getId(), eventFullDto.getTitle());
        return eventFullDto;
    }

    @Override
    public EventFullDto findEventByOwner(long ownerId, long eventId) throws NoSuchElementException {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (event.getInitiator().getId() != ownerId)
            throw new InvalidParameterException(String.format("Not found events for user with id %d", ownerId));
        log.info("Found event with id {}", event.getId());
        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateByUser(long ownerId, long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (event.getState().equals(State.PUBLISHED)) {
            throw new DataBaseException(String.format("Event with id = %d has no available for update", eventId));
        }
        if (event.getInitiator().getId() != ownerId)
            throw new DataBaseException(String.format("User with id = %d has no available event", ownerId));
        if (request.getEventDate() != null) {
            checkEventDate(request.getEventDate(), 2);
            event.setEventDate(request.getEventDate());
        }

        UserStateAction stateAction = request.getStateAction();
        if (stateAction != null) {
            if (stateAction.equals(UserStateAction.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            } else if (stateAction.equals(UserStateAction.SEND_TO_REVIEW)) event.setState(State.PENDING);
        }

        Event updatedEvent = eventRepository.save(updateEvent(event, request));
        log.info("Event with id = {} update", updatedEvent.getId());
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public Collection<EventFullDto> findEventsByParam(List<Long> users, List<State> states, List<Long> categories,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Integer from, Integer size) {
        if (from > 0 && size > 0) from = from / size;
        Collection<Event> events = eventRepository.findEventsByParams(users, states, categories, rangeStart,
                rangeEnd, PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id")));
        log.info("Found {} events", events.size());
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(long eventId, UpdateEventAdminRequest request) {
        if (request.getEventDate() != null) checkEventDate(request.getEventDate(), 1);
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!event.getState().equals(State.PENDING))
            throw new ConflictException(String.format("Event can't update: current state %s", event.getState()));
        if (request.getStateAction().equals(AdminStateAction.REJECT_EVENT)
                && event.getState().equals(State.PUBLISHED))
            throw new ConflictException(String.format("Event can't reject: current state %S", event.getState()));
        if (request.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        if (request.getStateAction().equals(AdminStateAction.REJECT_EVENT))
            event.setState(State.CANCELED);
        Event editEvent = eventRepository.saveAndFlush(updateEvent(event, request));
        log.info("Event with id = {} update", editEvent.getId());
        return EventMapper.toEventFullDto(editEvent);
    }

    private void checkEventDate(LocalDateTime dateTime, long hours) {
        if (dateTime.minusHours(hours).isBefore(LocalDateTime.now()))
            throw new ConflictException(
                    String.format("eventDate can't be earlier than %d hours before current moment", hours));
    }

    private Event updateEvent(Event event, NewEvent newEvent) {
        if (newEvent.getCategory() != null) {
            Category category = categoryRepository.findById(newEvent.getCategory()).orElseThrow();
            event.setCategory(category);
        }
        if (newEvent.getAnnotation() != null) event.setAnnotation(newEvent.getAnnotation());
        if (newEvent.getDescription() != null) event.setDescription(newEvent.getDescription());
        if (newEvent.getLocation() != null) event.setLocation(newEvent.getLocation());
        if (newEvent.getPaid() != null) event.setPaid(true);
        if (newEvent.getParticipantLimit() != null) event.setParticipantLimit(newEvent.getParticipantLimit());
        if (newEvent.getRequestModeration() != null) event.setRequestModeration(newEvent.getRequestModeration());
        if (newEvent.getTitle() != null) event.setTitle(newEvent.getTitle());
        return event;
    }


}
