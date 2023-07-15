package ru.practicum.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.constant.StatusRequest;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class RequestMapper {
    public Request toRequest(Event event, User requester) {
        return Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(StatusRequest.PENDING).build();
    }

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus()).build();
    }
}
