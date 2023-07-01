package ru.practicum.adminApi.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adminApi.category.dao.CategoryRepository;
import ru.practicum.adminApi.category.dto.CategoryDto;
import ru.practicum.adminApi.category.dto.CategoryMapper;
import ru.practicum.adminApi.category.dto.NewCategoryDto;
import ru.practicum.adminApi.category.model.Category;
import ru.practicum.exception.DataBaseException;

import java.security.InvalidParameterException;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        CategoryDto categoryDto;
        try {
            categoryDto = CategoryMapper.toCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            log.warn("Category with name = {} already exists", category.getName());
            throw new DataBaseException(String.format("Category with name = %s already exists",
                    category.getName()));
        }
        log.info("Category with id = {} and name = {} successful created",
                category.getId(), category.getName());

        return categoryDto;
    }

    @Override
    public void delete(long catId) {
        if (categoryRepository.existsById(catId)) {
            categoryRepository.deleteById(catId);
            log.info("Category with id = {} deleted", catId);
        } else {
            log.warn("Deleting a category with id = {} is not possible, category not found", catId);
            throw new InvalidParameterException(String.format(
                    "Deleting a category with id = %s is not possible, category not found", catId));
        }
        // todo constraint 409
    }

    @Override
    public CategoryDto update(long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow();
        category.setName(newCategoryDto.getName());
        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.warn("Category with name = {} already exists", category.getName());
            throw new DataBaseException(String.format("Category with name = %s already exists",
                    category.getName()));
        }
        log.info("Category with id = {} and name = {} successful update",
                category.getId(), category.getName());
        return CategoryMapper.toCategoryDto(category);
    }
}
