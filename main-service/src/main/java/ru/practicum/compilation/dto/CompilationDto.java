package ru.practicum.compilation.dto;

import ru.practicum.event.dto.EventDto;

import java.util.Set;

public class CompilationDto {

    private Long id;

    private Boolean pinned;

    private String title;

    private Set<EventDto> events;
}
