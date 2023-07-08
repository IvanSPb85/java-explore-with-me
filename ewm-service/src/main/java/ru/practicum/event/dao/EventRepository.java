package ru.practicum.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.constant.StateEvent;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query("select e from Event e where e.initiator.id = ?1 order by e.id asc")
    List<Event> findAllByOwner(long ownerId, Pageable pageable);

//    @Query("select e from Event e " +
//            "where (e.initiator.id in :users or :users = null) " +
//            "and (e.state in :states or :states = null) " +
//            "and (e.category.id in :categories or :categories = null) "
//        )
//    List<Event> findEventsByParams(@Param("users") List<Long> users,
//                                   @Param("states") List<StateEvent> states,
//                                   @Param("categories") List<Long> categories,
//                                   @Param("rangeStart") LocalDateTime rangeStert,
//                                   @Param("rangeEnd") LocalDateTime rangeEnd,
//                                   Pageable pageable);

  
    }

