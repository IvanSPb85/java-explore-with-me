package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.constant.StateEvent;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.constant.Constant.YYYY_MM_DD_HH_MM_SS;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventFullDto {
    @JsonUnwrapped
    private ParticipantsEvent participantsEvent;
    @JsonUnwrapped
    private ChronologyEvent chronologyEvent;
    private String annotation;
    private CategoryDto category;
    private String description;
    private long id;
    private UserShortDto initiator;
    private Location location;
    private StateEvent state;
    private String title;
    private long views;

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class ParticipantsEvent {
        public long confirmedRequests;
        private int participantLimit;
        private boolean requestModeration;
        private boolean paid;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class ChronologyEvent {
        private LocalDateTime createdOn;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = YYYY_MM_DD_HH_MM_SS)
        private LocalDateTime eventDate;
        private LocalDateTime publishedOn;
    }
}

