package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;

import java.util.Collection;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    void delete(Long compId);

    CompilationDto update(long compId, NewCompilationDto newCompilationDto);

    CompilationDto findById(long compId);

    Collection<CompilationDto> findAllByParam(boolean pinned, Integer from, Integer size);
}
