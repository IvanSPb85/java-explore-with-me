package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.transfer.New;
import ru.practicum.transfer.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    @NotBlank(groups = {New.class})
    @Size(min = 1, max = 50, groups = {New.class, Update.class})
    private String title;
}
