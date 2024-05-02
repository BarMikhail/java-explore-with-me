package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.additionally.Create;
import ru.practicum.additionally.Update;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Validated(Create.class) CategoryDto categoryDto) {
        log.info("");
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto updateCategory(@RequestBody @Validated(Update.class) CategoryDto categoryDto,
                                      @PathVariable("catId") Long categoryId) {
        log.info("");
        return categoryService.updateCategory(categoryDto, categoryId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long categoryId) {
        log.info("");
        categoryService.deleteCategory(categoryId);
    }

//    @GetMapping
//    @ResponseStatus(value = HttpStatus.OK)
//    public List<UserDto> getUsers(@RequestParam(defaultValue = "") List<Long> id,
//                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
//                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
//        log.info("Получение списка пользователей");
//        return userService.getUsers(id, from, size);
//    }
//
//    @DeleteMapping("/{userId}")
//    @ResponseStatus(value = HttpStatus.NO_CONTENT)
//    public void deleteUser(@PathVariable("userId") Long userId) {
//        log.info("Удаление {} юзера", userId);
//        userService.deleteUser(userId);
//    }
//
//    @PostMapping
//    @ResponseStatus(value = HttpStatus.CREATED)
//    public UserDto createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
//        log.info("Создание юзера");
//        return userService.createUser(userDto);
//    }
}
