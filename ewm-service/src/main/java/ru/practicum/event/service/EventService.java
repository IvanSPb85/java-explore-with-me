package ru.practicum.event.service;

import ru.practicum.constant.StateEvent;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    Collection<EventShortDto> findEventsByOwner(long ownerId, Integer from, Integer size);

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto findEventByOwner(long ownerId, long eventId);

    EventFullDto updateByUser(long ownerId, long eventId, UpdateEventUserRequest updateEvent);

    Collection<EventFullDto> findEventsByParam(List<Long> users, List<StateEvent> states,
                                               List<Long> categories, LocalDateTime rangeStart,
                                               LocalDateTime rangeEndInteger,
                                               Integer from, Integer size);

    EventFullDto updateByAdmin(long eventId, UpdateEventAdminRequest updateEvent);
}
