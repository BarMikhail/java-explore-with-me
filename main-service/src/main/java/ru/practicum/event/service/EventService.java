package ru.practicum.event.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

@Repository
public interface EventService extends JpaRepository<Event,Long> {

    boolean existsByCategoryId(Long catId);
}
