package ru.practicum.privateApi.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adminApi.category.dao.CategoryRepository;
import ru.practicum.adminApi.category.model.Category;
import ru.practicum.adminApi.user.dao.UserRepository;
import ru.practicum.adminApi.user.model.User;
import ru.practicum.exception.DataBaseException;
import ru.practicum.privateApi.event.dao.EventRepository;
import ru.practicum.privateApi.event.dto.EventFullDto;
import ru.practicum.privateApi.event.dto.EventMapper;
import ru.practicum.privateApi.event.dto.EventShortDto;
import ru.practicum.privateApi.event.dto.NewEventDto;
import ru.practicum.privateApi.event.model.Event;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Collection;
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
        checkEventDate(newEventDto);
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
    public EventFullDto update(long ownerId, long eventId, EventShortDto eventShortDto) {

        return null;
    }

    private void checkEventDate(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().minusHours(2).isBefore(LocalDateTime.now()))
            throw new InvalidParameterException("eventDate can't be earlier than two hours before current moment");
    }
}
