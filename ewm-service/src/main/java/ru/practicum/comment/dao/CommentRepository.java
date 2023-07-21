package ru.practicum.comment.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.comment.dto.CommentsCounter;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    List<Comment> findAllByCommentatorId(long userId, Pageable pageable);

    List<Comment> findAllByEventId(long eventId, Pageable pageable);

    @Query("select new ru.practicum.comment.dto.CommentsCounter(c.event.id, count(*)) " +
            "from Comment c group by c.event.id")
    List<CommentsCounter> findAllCommentsCounter();
}
