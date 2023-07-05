package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(long catId);

    CategoryDto update(long catId, NewCategoryDto newCategoryDto);

    Collection<CategoryDto> findAllByParam(Integer from, Integer size);

    CategoryDto findById(long catId);
}
