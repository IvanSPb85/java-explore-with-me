package ru.practicum.event.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.constant.Constant.REQUEST_GET_LOG;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    private static final String SERViCE_NAME = "ewm-service";

    @GetMapping
    public ResponseEntity<Collection<EventShortDto>> findAllByParam(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(eventService.findAllByParam(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> findById(@PathVariable long id,
                                                 HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        statsClient.create(EndpointHitDto.builder()
                .app(SERViCE_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build());
        return new ResponseEntity<>(eventService.findById(id, request), HttpStatus.OK);
    }
}
