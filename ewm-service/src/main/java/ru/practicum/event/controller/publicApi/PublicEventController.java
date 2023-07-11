package ru.practicum.event.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.StatsClient;
import ru.practicum.constant.StateEvent;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.PredicateParam;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.constant.Constant.YYYY_MM_DD_HH_MM_SS;


@Slf4j
@RequiredArgsConstructor
@Controller
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
            @RequestParam(required = false) @DateTimeFormat(pattern = YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @PositiveOrZero Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        sendStats(request);
        PredicateParam param = PredicateParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart != null ? rangeStart : LocalDateTime.now())
                .rangeEnd(rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(1000))
                .onlyAvailable(onlyAvailable)
                .state(StateEvent.PUBLISHED)
                .sort(sort).build();
        return new ResponseEntity<>(eventService.findAllByParamForPublic(param, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> findById(@PathVariable long id,
                                                 HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        sendStats(request);
        return new ResponseEntity<>(eventService.findById(id, request), HttpStatus.OK);
    }

    private void sendStats(HttpServletRequest request) {
        statsClient.create(EndpointHitDto.builder()
                .app(SERViCE_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build());
    }
}
