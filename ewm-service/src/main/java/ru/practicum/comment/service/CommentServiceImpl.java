package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.transfer.PageSort.getPageable;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public CommentDto create(long userId, long eventId, NewCommentDto newComment) {
        User user = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        Comment comment = CommentMapper.toComment(newComment, user, event);
        CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        log.info("Comment with id '{}' created", commentDto.getId());
        return commentDto;
    }

    @Transactional
    @Override
    public CommentDto update(long userId, long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(userId).orElseThrow();
        if (comment.getCommentator().getId() != userId)
            throw new ConflictException(String.format("User with id '%d' does not have access to comment id '%d'",
                    userId, commentId));
        comment.setText(newCommentDto.getText());
        commentRepository.save(comment);
        log.info("Comment id '{}' updated", commentId);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public Collection<CommentDto> findAllByOwner(long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow();
        Pageable pageable = getPageable(from, size, Sort.by(Sort.Direction.DESC, "posted"));
        Collection<Comment> comments = commentRepository.findAllById(userId, pageable);
        log.info("For User id '{}' found '{}' comments", userId, comments.size());
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}
