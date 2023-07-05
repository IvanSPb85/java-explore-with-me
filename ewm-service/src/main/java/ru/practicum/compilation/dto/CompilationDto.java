package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompilationDto {
    private Set<EventShortDto> events;
    private long id;
    private boolean pinned;
    private String title;
}
