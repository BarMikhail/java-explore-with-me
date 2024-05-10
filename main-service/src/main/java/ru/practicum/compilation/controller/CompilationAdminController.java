package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.additionally.Create;
import ru.practicum.additionally.Update;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;
import ru.practicum.compilation.service.CompilationService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Validated(Create.class) CompilationDtoNew compilationDtoNew) {

        log.info("Создание сборника");
        return compilationService.createCompilation(compilationDtoNew);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {
        log.info("Удаление сборника с id {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @Validated(Update.class) CompilationDtoNew compilationDtoNew) {
        log.info("Обновление сборника с id {}", compId);
        return compilationService.updateCompilation(compId, compilationDtoNew);
    }
}
