package ru.practicum.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle()).build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .events(compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toSet()))
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle()).build();
    }
}
