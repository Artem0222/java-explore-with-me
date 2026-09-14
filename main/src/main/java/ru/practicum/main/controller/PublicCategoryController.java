package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /categories: from={}, size={}", from, size);
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoriesById(@PathVariable Long catId) {
        log.info("GET /categories/{}", catId);
        return categoryService.getCategoryById(catId);
    }
}
