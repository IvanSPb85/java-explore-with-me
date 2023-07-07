package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.exception.ConflictException;

import java.util.Collection;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto) throws ConflictException;

    void delete(Long compId);

    CompilationDto update(long compId, NewCompilationDto newCompilationDto);

    CompilationDto findById(long compId);

    Collection<CompilationDto> findAllByParam(Boolean pinned, Integer from, Integer size);
}
