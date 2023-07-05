package ru.practicum.compilation.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static ru.practicum.constant.Constant.REQUEST_DELETE_LOG;
import static ru.practicum.constant.Constant.REQUEST_PATCH_LOG;
import static ru.practicum.constant.Constant.REQUEST_POST_LOG;


@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/admin/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> create(
            @RequestBody @Valid NewCompilationDto newCompilationDto,
            HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(compilationService.create(newCompilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> delete(Long compId, HttpServletRequest request) {
        log.info(REQUEST_DELETE_LOG, request.getRequestURI());
        compilationService.delete(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> update(
            @RequestBody @Valid NewCompilationDto newCompilationDto,
            Long compId, HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(compilationService.update(compId, newCompilationDto), HttpStatus.OK);
    }
}
