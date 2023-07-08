package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.constant.StateEvent;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class PredicateParam {
    private String text;
    private List<Long> users;
    private List<StateEvent> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean paid;
    private Boolean onlyAvailable;
    private String sort;
    private StateEvent state;
}
