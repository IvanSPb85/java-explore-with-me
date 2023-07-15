package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.exception.ConflictException;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.transfer.PageSort.getPageable;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        CategoryDto categoryDto;
        try {
            categoryDto = CategoryMapper.toCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(String.format("Category with name = '%s' already exists",
                    category.getName()), exception);
        }
        log.info("Category with id = '{}' and name = '{}' successful created",
                category.getId(), category.getName());

        return categoryDto;
    }

    @Override
    @Transactional
    public void delete(long catId) {
        try {
            categoryRepository.deleteById(catId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("Category with Id '%d' has constraint events", catId));
        }
        log.info("Category with Id = '{}' deleted", catId);
    }

    @Override
    @Transactional
    public CategoryDto update(long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow();
        category.setName(newCategoryDto.getName());
        try {
            categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(String.format("Category with name = %s already exists",
                    category.getName()), exception);
        }
        log.info("Category with id = '{}' and name = '{}' successful update",
                category.getId(), category.getName());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public Collection<CategoryDto> findAllByParam(Integer from, Integer size) {
        Pageable pageable = getPageable(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Collection<Category> categories = categoryRepository.findAll(pageable).toList();
        log.info("Found '{}' categories", categories.size());
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow();
        log.info("Found category with Id '{}' and name '{}'", catId, category.getName());
        return CategoryMapper.toCategoryDto(category);
    }
}
