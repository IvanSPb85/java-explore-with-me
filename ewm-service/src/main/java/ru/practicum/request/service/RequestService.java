package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.Collection;

public interface RequestService {
    Collection<ParticipationRequestDto> findAllByUser(long userId);

    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
