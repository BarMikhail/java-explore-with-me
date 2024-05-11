package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilation(CompilationDtoNew compilationDtoNew) {
        return Compilation.builder()
                .title(compilationDtoNew.getTitle())
                .pinned(compilationDtoNew.getPinned())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {

        List<EventShortDto> eventShortDtoList = EventMapper.toEventShortDtoList(compilation.getEvents());

        Set<EventShortDto> eventShortDtoSet = new HashSet<>();

        for (EventShortDto shortDto : eventShortDtoList) {
            eventShortDtoSet.add(shortDto);
        }

        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(eventShortDtoSet)
                .build();
    }

    public Set<CompilationDto> toCompilationDtoSet(Iterable<Compilation> compilations) {
        Set<CompilationDto> result = new HashSet<>();

        for (Compilation compilation : compilations) {
            result.add(toCompilationDto(compilation));
        }
        return result;
    }
}
