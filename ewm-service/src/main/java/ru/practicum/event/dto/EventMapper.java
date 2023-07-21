package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.constant.StateEvent;
import ru.practicum.event.model.Event;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Map;

@UtilityClass
public class EventMapper {
    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews()).build();
    }

    public EventShortDto toEventShortCommentsDto(Event event, Map<Long, Long> comments) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .comments(comments.getOrDefault(event.getId(), 0L)).build();
    }

    public Event toEvent(NewEventDto newEventDto, Category category, User initiator) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false)
                .participantLimit(newEventDto.getParticipantLimit() != null
                        ? newEventDto.getParticipantLimit() : 0)
                .requestModeration(newEventDto.getRequestModeration() != null
                        ? newEventDto.getRequestModeration() : true)
                .state(StateEvent.PENDING)
                .title(newEventDto.getTitle())
                .views(0L).build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .participantsEvent(EventFullDto.ParticipantsEvent.builder()
                        .confirmedRequests(event.getConfirmedRequests())
                        .participantLimit(event.getParticipantLimit())
                        .requestModeration(event.isRequestModeration())
                        .paid(event.isPaid()).build())
                .chronologyEvent(EventFullDto.ChronologyEvent.builder()
                        .createdOn(event.getCreatedOn())
                        .eventDate(event.getEventDate())
                        .publishedOn(event.getPublishedOn()).build())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .description(event.getDescription())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews()).build();
    }
}
