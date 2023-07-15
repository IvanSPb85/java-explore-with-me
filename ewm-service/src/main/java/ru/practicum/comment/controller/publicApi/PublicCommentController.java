package ru.practicum.comment.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.constant.Constant.REQUEST_GET_LOG;

@Slf4j
@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping("/comments/{eventId}")
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<Collection<CommentDto>> findAllByAdmin(
            @PathVariable long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @PositiveOrZero Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request);
        return new ResponseEntity<>(commentService.findAllForEvent(eventId, from, size), HttpStatus.OK);
    }
}
