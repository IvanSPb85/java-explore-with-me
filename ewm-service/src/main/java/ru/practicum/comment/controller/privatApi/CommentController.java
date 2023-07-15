package ru.practicum.comment.controller.privatApi;

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
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

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
@RequestMapping("/users/{userId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    public ResponseEntity<CommentDto> create(@PathVariable long userId,
                                             @PathVariable long eventId,
                                             @RequestBody @Valid NewCommentDto newCommentDto,
                                             HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(commentService.create(userId, eventId, newCommentDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(@PathVariable long userId,
                                             @PathVariable long commentId,
                                             @RequestBody @Valid NewCommentDto newCommentDto,
                                             HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request);
        return new ResponseEntity<>(commentService.update(userId, commentId, newCommentDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<CommentDto>> findAllByOwner(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @PositiveOrZero Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request);
        return new ResponseEntity<>(commentService.findAllByOwner(userId, from, size), HttpStatus.OK);
    }
}
