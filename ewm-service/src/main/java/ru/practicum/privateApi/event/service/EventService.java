package ru.practicum.privateApi.event.service;

import ru.practicum.constant.State;
import ru.practicum.privateApi.event.dto.EventFullDto;
import ru.practicum.privateApi.event.dto.EventShortDto;
import ru.practicum.privateApi.event.dto.NewEventDto;
import ru.practicum.privateApi.event.dto.UpdateEventAdminRequest;
import ru.practicum.privateApi.event.dto.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    Collection<EventShortDto> findEventsByOwner(long ownerId, Integer from, Integer size);

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto findEventByOwner(long ownerId, long eventId);

    EventFullDto updateByUser(long ownerId, long eventId, UpdateEventUserRequest updateEvent);

    Collection<EventFullDto> findEventsByParam(List<Long> users, List<State> states,
                                               List<Long> categories, LocalDateTime rangeStart,
                                               LocalDateTime rangeEndInteger,
                                               Integer from, Integer size);

    EventFullDto updateByAdmin(long eventId, UpdateEventAdminRequest updateEvent);
}
