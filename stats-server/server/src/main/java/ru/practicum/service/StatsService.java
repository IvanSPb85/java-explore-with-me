package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsService {
    EndpointHitDto create(EndpointHitDto endpointHitDto);

    Collection<ViewStatsDto> findStatsOfPeriod(LocalDateTime start, LocalDateTime end,
                                               List<String> uris, Boolean unique);
}
