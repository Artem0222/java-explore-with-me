package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория с названием " + newCategoryDto.getName() + " уже существует");
        }

        Category category = categoryMapper.toEntity(newCategoryDto);
        category = categoryRepository.save(category);
        log.info("Добавлена категория: {}", category);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("категория с ид=" + catId + "не найденп"));
        if (!category.getName().equals(categoryDto.getName()) && categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("категория с названием " + categoryDto.getName() + " уже существует");
        }

        category.setName(category.getName());
        category = categoryRepository.save(category);
        log.info("updated category: {}", category);
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Каткегория с ид " + catId + " не найдега"));
        boolean hasEvents = eventRepository.existsByCategoryId(catId);
        if (hasEvents) {
            throw new ConflictException("Эта категория не пуста");
        }

        categoryRepository.delete(category);
        log.info("удалена катекгория с ид={}", catId);

    }

}
