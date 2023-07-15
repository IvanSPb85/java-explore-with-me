package ru.practicum.request.controller.privateApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static ru.practicum.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.constant.Constant.REQUEST_PATCH_LOG;
import static ru.practicum.constant.Constant.REQUEST_POST_LOG;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<Collection<ParticipationRequestDto>> getAllByUser(
            @PathVariable long userId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(requestService.findAllByUser(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> create(@PathVariable long userId,
                                                          @RequestParam long eventId,
                                                          HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(requestService.create(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable long userId,
                                                                 @PathVariable long requestId,
                                                                 HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(requestService.cancelRequest(userId, requestId), HttpStatus.OK);
    }
}
