package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.PredicateParam;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface EventService {
    Collection<EventShortDto> findEventsByOwner(long ownerId, Integer from, Integer size);

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto findEventByOwner(long ownerId, long eventId);

    EventFullDto updateByUser(long ownerId, long eventId, UpdateEventUserRequest updateEvent);

    EventFullDto updateByAdmin(long eventId, UpdateEventAdminRequest updateEvent);

    Collection<ParticipationRequestDto> findRequestsForEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequests(long userId, long eventId, EventRequestStatusUpdateRequest request);

    Collection<EventShortDto> findAllByParamForPublic(PredicateParam param,
                                                      Integer from, Integer size, HttpServletRequest request);

    EventFullDto findById(long eventId, HttpServletRequest request);

    Collection<EventFullDto> findAllByParamForAdmin(PredicateParam param, Integer from, Integer size);
}
