package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.constant.Constant.REQUEST_POST_LOG;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> create(@RequestBody EndpointHitDto endpointHitDto,
                                                 HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(statsService.create(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<Collection<ViewStatsDto>> getStatsOfPeriod(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique, HttpServletRequest request

    ) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(statsService.findStatsOfPeriod(start, end, uris, unique), HttpStatus.OK);
    }
}
