package ru.practicum.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.constant.StateEvent;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where e.initiator.id = ?1 order by e.id asc")
    List<Event> findAllByOwner(long ownerId, Pageable pageable);

    @Query("select e from Event e where e.initiator.id in ?1 and e.state in ?2 and e.category.id in ?3 " +
            "and e.eventDate between ?4 and ?5")
    List<Event> findEventsByParams(List<Long> users, List<StateEvent> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEndInteger, Pageable pageable);
}
