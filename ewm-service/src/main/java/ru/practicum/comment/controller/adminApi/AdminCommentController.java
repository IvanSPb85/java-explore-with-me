package ru.practicum.comment.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentParam;
import ru.practicum.comment.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.constant.Constant.REQUEST_DELETE_LOG;
import static ru.practicum.constant.Constant.REQUEST_GET_LOG;

@Slf4j
@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable long commentId,
                                       HttpServletRequest request) {
        log.info(REQUEST_DELETE_LOG, request);
        commentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Collection<CommentDto>> findAllByAdmin(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @PositiveOrZero Integer size,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {
        CommentParam param = CommentParam.builder()
                .from(from)
                .size(size)
                .eventId(eventId)
                .userId(userId).build();
        log.info(REQUEST_GET_LOG, request);
        return ResponseEntity.ok(commentService.findAllByAdmin(param));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> findComment(
            @PathVariable long commentId,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request);
        return ResponseEntity.ok(commentService.findComment(commentId));
    }
}
