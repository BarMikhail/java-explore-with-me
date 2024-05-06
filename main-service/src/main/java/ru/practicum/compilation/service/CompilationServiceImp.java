package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImp implements CompilationService {
    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(CompilationDtoNew compilationDtoNew) {
        Compilation compilation = CompilationMapper.toCompilation(compilationDtoNew);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilationDtoNew.getEvents() == null || compilationDtoNew.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(compilationDtoNew.getEvents()));
        }

        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long comId) {
        checkCompilation(comId);
        compilationRepository.deleteById(comId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long comId, CompilationDtoNew compilationDtoNew) {

        Compilation compilation = checkCompilation(comId);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        if (compilationDtoNew.getEvents() == null || compilationDtoNew.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(compilationDtoNew.getEvents()));
        }

        if (compilationDtoNew.getTitle() != null) {
            compilation.setTitle(compilationDtoNew.getTitle());
        }

        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned) {
            compilations = compilationRepository.findByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }
        return new ArrayList<>(CompilationMapper.toCompilationDtoSet(compilations));
    }

    @Override
    public CompilationDto getCompilationById(Long comId) {
        Compilation compilation = checkCompilation(comId);

        return CompilationMapper.toCompilationDto(compilation);
    }

    private Compilation checkCompilation(Long comId) {
        return compilationRepository.findById(comId).orElseThrow(() -> new NotFoundException("Нет такого юзера"));
    }
}
