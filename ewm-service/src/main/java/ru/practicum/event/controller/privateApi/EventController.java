package ru.practicum.event.controller.privateApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.constant.Constant.REQUEST_PATCH_LOG;
import static ru.practicum.constant.Constant.REQUEST_POST_LOG;


@Slf4j
@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping("/users/{userId}/events")
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Collection<EventShortDto>> getEventsByOwner(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @PositiveOrZero Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(eventService.findEventsByOwner(userId, from, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> create(@PathVariable long userId,
                                               @RequestBody @Valid NewEventDto newEventDto,
                                               HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(eventService.create(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventByOwner(@PathVariable long userId,
                                                        @PathVariable long eventId,
                                                        HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(eventService.findEventByOwner(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByOwner(@PathVariable long userId,
                                                           @PathVariable long eventId,
                                                           @RequestBody @Valid UpdateEventUserRequest eventUserRequest,
                                                           HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(eventService.updateByUser(userId, eventId, eventUserRequest), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<Collection<ParticipationRequestDto>> getRequestsForEvent(
            @PathVariable long userId,
            @PathVariable long eventId,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(eventService.findRequestsForEvent(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequests(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody EventRequestStatusUpdateRequest requestUpdate,
            HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(eventService.updateRequests(userId, eventId, requestUpdate), HttpStatus.OK);
    }
}
