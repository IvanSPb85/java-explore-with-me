package ru.practicum.privateApi.event.service;

import ru.practicum.privateApi.event.dto.EventFullDto;
import ru.practicum.privateApi.event.dto.EventShortDto;
import ru.practicum.privateApi.event.dto.NewEventDto;
import ru.practicum.privateApi.event.dto.UpdateEventUserRequest;

import java.util.Collection;

public interface EventService {
    Collection<EventShortDto> findEventsByOwner(long ownerId, Integer from, Integer size);

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto findEventByOwner(long ownerId, long eventId);

    EventFullDto update(long ownerId, long eventId, UpdateEventUserRequest updateEvent);
}
