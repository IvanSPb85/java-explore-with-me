package ru.practicum.comment.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.CommentParam;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.QComment;
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
        if (event.getInitiator().getId() == userId)
            throw new ConflictException("User can't commit own event");
        Comment comment = CommentMapper.toComment(newComment, user, event);
        CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        log.info("Comment with id '{}' created", commentDto.getId());
        return commentDto;
    }

    @Transactional
    @Override
    public CommentDto update(long userId, long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
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
        Collection<Comment> comments = commentRepository.findAllByCommentatorId(userId, pageable);
        log.info("For User id '{}' found '{}' comments", userId, comments.size());
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long commentId) {
        commentRepository.deleteById(commentId);
        log.info("Comment with id '{}' deleted", commentId);
    }

    @Override
    public CommentDto findComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        log.info("Comment with id '{}' found", commentId);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public Collection<CommentDto> findAllByAdmin(CommentParam param) {
        Pageable pageable = getPageable(param.getFrom(), param.getSize(),
                Sort.by(Sort.Direction.DESC, "posted"));
        Collection<CommentDto> comments;
        BooleanBuilder builder = new BooleanBuilder();
        if (param.getUserId() != null) builder.and(QComment.comment.commentator.id.eq(param.getUserId()));
        if (param.getEventId() != null) builder.and(QComment.comment.event.id.eq(param.getEventId()));
        if (builder.getValue() != null) {
            comments = commentRepository.findAll(builder.getValue(), pageable)
                    .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        } else {
            comments = commentRepository.findAll(pageable)
                    .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        }
        log.info("Found '{}' comments", comments.size());
        return comments;
    }

    @Override
    public Collection<CommentDto> findAllForEvent(long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        Pageable pageable = getPageable(from, size, Sort.by(Sort.Direction.DESC, "posted"));
        Collection<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);
        log.info("Found '{}' comments", comments.size());
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}
