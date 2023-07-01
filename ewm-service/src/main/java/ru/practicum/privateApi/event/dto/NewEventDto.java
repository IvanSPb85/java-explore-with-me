package ru.practicum.privateApi.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.privateApi.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewEventDto {
    @Length(min = 20, max = 2000)
    @NotBlank
    private String annotation;
    @NotNull
    private Long category;
    @Length(min = 20, max = 7000)
    @NotBlank
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @Length(min = 3, max = 120)
    @NotBlank
    private String title;
}
