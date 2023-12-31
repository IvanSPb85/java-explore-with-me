package ru.practicum.category.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.constant.Constant.REQUEST_GET_LOG;


@Slf4j
@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Collection<CategoryDto>> findAllByParams(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @PositiveOrZero Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(categoryService.findAllByParam(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable long catId,
                                                HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(categoryService.findById(catId), HttpStatus.OK);
    }
}
