package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CommentParam {
    private Integer from;
    private Integer size;
    private Long eventId;
    private Long userId;
}
