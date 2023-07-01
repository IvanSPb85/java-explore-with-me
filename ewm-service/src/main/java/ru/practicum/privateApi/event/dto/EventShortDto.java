package ru.practicum.privateApi.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.adminApi.category.dto.CategoryDto;
import ru.practicum.adminApi.user.dto.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    private LocalDateTime eventDate;
    private long id;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private long views;
}
