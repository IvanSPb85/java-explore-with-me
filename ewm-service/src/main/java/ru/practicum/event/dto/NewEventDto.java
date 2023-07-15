package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.constant.Constant.YYYY_MM_DD_HH_MM_SS;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewEventDto extends NewEvent {
    @Length(min = 20, max = 2000)
    @NotBlank
    private String annotation;
    @NotNull
    private Long category;
    @Length(min = 20, max = 7000)
    @NotBlank
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    @Length(min = 3, max = 120)
    @NotBlank
    private String title;
}
