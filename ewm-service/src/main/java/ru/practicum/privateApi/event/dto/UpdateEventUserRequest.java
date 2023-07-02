package ru.practicum.privateApi.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.constant.UserStateAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateEventUserRequest extends NewEvent {
    private UserStateAction stateAction;
}
