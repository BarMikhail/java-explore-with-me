package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category category = checkCategory(categoryId);
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        checkCategory(categoryId);
        if (!eventRepository.findByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException("Эта категория испльзуется!");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return CategoryMapper.toCategoryDtoList(categoryRepository.findAll(pageRequest));
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(checkCategory(categoryId));
    }

    private Category checkCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Нет такой категории"));
    }
}
