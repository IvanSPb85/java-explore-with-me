package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.constant.StatusRequestUpdate;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventRequestStatusUpdateRequest {
    List<Long> requestIds;
    StatusRequestUpdate status;
}
