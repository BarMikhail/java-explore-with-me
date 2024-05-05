//package ru.practicum.compilation.controller;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import ru.practicum.additionally.Create;
//import ru.practicum.additionally.Update;
//import ru.practicum.compilation.dto.CompilationDto;
//import ru.practicum.compilation.dto.CompilationDtoNew;
//import ru.practicum.compilation.service.CompilationService;
//
//@Slf4j
//@Validated
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/admin/compilations")
//public class CompilationAdminController {
//
//    private final CompilationService compilationService;
//
//    @PostMapping
//    @ResponseStatus(value = HttpStatus.CREATED)
//    public CompilationDto createCompilation(@RequestBody @Validated(Create.class) CompilationDtoNew compilationDtoNew) {
//
//        log.info("");
//        return compilationService.createCompilation(compilationDtoNew);
//    }
//
//    @DeleteMapping("/{comId}")
//    @ResponseStatus(value = HttpStatus.NO_CONTENT)
//    public void deleteCompilation(@PathVariable("comId") Long comId) {
//        log.info("");
//        compilationService.deleteCompilation(comId);
//    }
//
//    @PatchMapping("/{comId}")
//    @ResponseStatus(value = HttpStatus.OK)
//    public CompilationDto updateCompilation(@PathVariable Long comId,
//                                            @RequestBody @Validated(Update.class) CompilationDtoNew compilationDtoNew) {
//        log.info("");
//        return compilationService.updateCompilation(comId, compilationDtoNew);
//    }
//}
