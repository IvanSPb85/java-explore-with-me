package ru.practicum.request.dto;

import ru.practicum.constant.StatusRequest;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class RequestMapper  {
    public static Request toRequest(Event event, User requester) {
        return Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(StatusRequest.PENDING).build();
    }

    public static ParticipationRequestDto toParticipationRequestDto (Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus()).build();
    }
}
