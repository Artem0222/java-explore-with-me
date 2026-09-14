package ru.practicum.main.service;

import ru.practicum.main.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(Long catId);
}