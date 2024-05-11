package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, RequestStatus status);
}
