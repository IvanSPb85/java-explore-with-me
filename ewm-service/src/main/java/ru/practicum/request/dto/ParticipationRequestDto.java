package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.constant.StatusRequest;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ParticipationRequestDto {
    private LocalDateTime created;
    private long event;
    private long id;
    private long requester;
    private StatusRequest status;
}
