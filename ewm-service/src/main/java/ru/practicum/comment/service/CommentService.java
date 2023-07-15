package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.Collection;

public interface CommentService {
    CommentDto create(long userId, long eventId, NewCommentDto newComment);

    CommentDto update(long userId, long commentId, NewCommentDto newComment);

    Collection<CommentDto> findAllByOwner(long userId, Integer from, Integer size);

}
