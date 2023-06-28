package ru.practicum.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, count(eh.ip)) " +
            "from EndpointHit eh where eh.timestamp between ?1 and ?2 " +
            "and (eh.uri in ?3 or ?3 = null) group by eh.app, eh.uri " +
            "order by count(eh.ip) desc")
    List<ViewStatsDto> findViewStatsDtoOfPeriod(LocalDateTime start, LocalDateTime end,
                                                List<String> uris, Pageable pageable);

    @Query("select new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit eh where eh.timestamp between ?1 and ?2 " +
            "and (eh.uri in ?3 or ?3 = null) group by eh.app, eh.uri " +
            "order by count(distinct eh.ip) desc")
    List<ViewStatsDto> findUniqueViesStatsDtoOfPeriod(LocalDateTime start, LocalDateTime end,
                                                      List<String> uris, Pageable pageable);
}
