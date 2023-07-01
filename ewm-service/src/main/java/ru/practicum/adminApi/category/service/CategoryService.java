package ru.practicum.adminApi.category.service;

import ru.practicum.adminApi.category.dto.CategoryDto;
import ru.practicum.adminApi.category.dto.NewCategoryDto;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(long catId);

    CategoryDto update(long catId, NewCategoryDto newCategoryDto);
}
