package ru.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.Collection;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findAllByRequesterId(long userId);

    Optional<Request> findByRequesterIdAndId(long userId, long requestId);

    Collection<Request> findAllByEventId(long eventId);
}
