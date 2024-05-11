package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(CompilationDtoNew compilationDtoNew);

    void deleteCompilation(Long comId);

    CompilationDto updateCompilation(Long comId, CompilationDtoNew compilationDtoNew);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long comId);
}
