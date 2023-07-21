package ru.practicum.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

@UtilityClass
public class CommentMapper {
    public Comment toComment(NewCommentDto newComment, User user, Event event) {
        return Comment.builder()
                .event(event)
                .commentator(user)
                .text(newComment.getText()).build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .commentator(comment.getCommentator())
                .text(comment.getText())
                .posted(comment.getPosted())
                .updated(comment.getUpdated()).build();
    }
}
