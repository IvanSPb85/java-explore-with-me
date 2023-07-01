package ru.practicum.adminApi.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.adminApi.category.dto.CategoryDto;
import ru.practicum.adminApi.category.dto.NewCategoryDto;
import ru.practicum.adminApi.category.service.CategoryService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static ru.practicum.constant.Constant.REQUEST_DELETE_LOG;
import static ru.practicum.constant.Constant.REQUEST_PATCH_LOG;
import static ru.practicum.constant.Constant.REQUEST_POST_LOG;


@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/admin/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid NewCategoryDto newCategoryDto,
                                              HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(categoryService.create(newCategoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> delete(@PathVariable long catId, HttpServletRequest request) {
        log.info(REQUEST_DELETE_LOG, request.getRequestURI());
        categoryService.delete(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> update(@PathVariable long catId,
                                              @RequestBody @Valid NewCategoryDto newCategoryDto,
                                              HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(categoryService.update(catId, newCategoryDto), HttpStatus.OK);
    }
}
