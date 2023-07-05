package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.exception.DataBaseException;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
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
    @Transactional
    public void delete(long catId) {
        if (categoryRepository.existsById(catId)) {
            categoryRepository.deleteById(catId);
        } else {
            log.warn("Deleting a category with id = {} is not possible, category not found", catId);
            throw new InvalidParameterException(String.format(
                    "Deleting a category with id = %s is not possible, category not found", catId));
        }
        log.info("Category with id = {} deleted", catId);


        // todo constraint 409
    }

    @Override
    @Transactional
    public CategoryDto update(long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow();
        category.setName(newCategoryDto.getName());
        try {
            categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException e) {
            log.warn("Category with name = {} already exists", category.getName());
            throw new DataBaseException(String.format("Category with name = %s already exists",
                    category.getName()));
        }
        log.info("Category with id = {} and name = {} successful update",
                category.getId(), category.getName());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public Collection<CategoryDto> findAllByParam(Integer from, Integer size) {
        if (from > 0 && size > 0) from = from / size;
        Collection<Category> categories = categoryRepository
                .findAll(PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"))).toList();
        log.info("Found '{}' categories", categories.size());
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow();
        log.info("Found category with name '{}'", category.getName());
        return CategoryMapper.toCategoryDto(category);
    }


}
