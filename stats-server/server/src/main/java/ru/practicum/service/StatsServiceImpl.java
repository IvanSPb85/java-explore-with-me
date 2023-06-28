package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dao.StatsRepository;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        log.info("Endpoint {} with id {} saved.", endpointHit.getUri(), endpointHit.getId());
        return EndpointHitMapper.toEndpointHitDto(endpointHit);
    }

    @Override
    public Collection<ViewStatsDto> findStatsOfPeriod(LocalDateTime start, LocalDateTime end,
                                                      List<String> uris, Boolean unique) {
        if (start.isAfter(end)) throw new InvalidParameterException("start can't be after end");
        Collection<ViewStatsDto> result;
        if (unique) result = statsRepository.findUniqueViesStatsDtoOfPeriod(start, end, uris,
                PageRequest.of(0, 10));
        else result = statsRepository.findViewStatsDtoOfPeriod(start, end, uris,
                PageRequest.of(0, 10));
        log.info("Found {} requests", result.size());
        return result;
    }
}
