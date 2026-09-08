package ru.practicum.main.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.service.AdminCategoryService;

@RestController
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("POST /admin/categories: {}", newCategoryDto);
        return adminCategoryService.addCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(
            @PathVariable Long catId,
            @Valid @RequestBody CategoryDto categoryDto
    ) {
        log.info("PATCH /admin/categories/{}:   {}", catId, categoryDto);
        return adminCategoryService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("DELETE /admin/categories/{}", catId);
        adminCategoryService.deleteCategory(catId);
    }
}
