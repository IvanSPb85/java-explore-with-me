package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.constant.StateEvent;
import ru.practicum.constant.StatusRequest;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Collection<ParticipationRequestDto> findAllByUser(long userId) {
        userRepository.findById(userId).orElseThrow();
        Collection<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Found '{}' requests", requests.size());
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto create(long userId, long eventId) {
        User requester = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        Request request = RequestMapper.toRequest(event, requester);
        if (event.getInitiator().getId() == userId)
            throw new ConflictException(String.format(
                    "The initiator Id '%d' of the event Id '%d' cannot add a request to participate in his event",
                    userId, eventId));
        if (!event.getState().equals(StateEvent.PUBLISHED))
            throw new ConflictException(String.format(
                    "You cannot participate in an unpublished event with Id '%d'", eventId));
        if (event.getConfirmedRequests() == event.getParticipantLimit() && event.getParticipantLimit() != 0L)
            throw new ConflictException(String.format(
                    "The event with Id '%d' has reached the limit of participation requests", eventId));
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
            request.setStatus(StatusRequest.CONFIRMED);
        }
        try {
            request = requestRepository.saveAndFlush(request);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("You cannot add a repeat request");
        }
        log.info("Request with id '{}' from userId '{}' for eventId '{}' created", request.getId(), userId, eventId);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId).orElseThrow();
        request.setStatus(StatusRequest.CANCELED);
        requestRepository.saveAndFlush(request);
        log.info("Request with Id '{}' canceled", requestId);
        return RequestMapper.toParticipationRequestDto(request);
    }
}
