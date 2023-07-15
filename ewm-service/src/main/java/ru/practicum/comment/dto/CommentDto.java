package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.constant.Constant.YYYY_MM_DD_HH_MM_SS;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommentDto {
    private long id;
    private EventShortDto event;
    private User commentator;
    private String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime posted;
}
