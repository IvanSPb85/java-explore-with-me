package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.constant.AdminStateAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateEventAdminRequest extends NewEvent {
    private AdminStateAction stateAction;
}
