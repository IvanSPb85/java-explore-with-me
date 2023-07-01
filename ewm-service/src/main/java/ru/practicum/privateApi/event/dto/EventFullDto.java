package ru.practicum.privateApi.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.adminApi.category.dto.CategoryDto;
import ru.practicum.adminApi.user.dto.UserShortDto;
import ru.practicum.constant.State;
import ru.practicum.privateApi.event.model.Location;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private long id;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private State state;
    private String title;
    private long views;
}
