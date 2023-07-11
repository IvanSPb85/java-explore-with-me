package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.exception.ConflictException;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.transfer.PageSort.getPageable;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Set<Event> events = findEventsForComp(newCompilationDto);
        if (newCompilationDto.getPinned() == null) newCompilationDto.setPinned(false);
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        try {
            compRepository.saveAndFlush(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    String.format("The name for title '%s' already exists", compilation.getTitle()));
        }
        log.info("Compilation with id '{}' and title '{}' created", compilation.getId(), compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        try {
            compRepository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidParameterException(String.format(
                    "Deleting a compilation with id = '%d' is not possible, compilation not found", compId));
        }
        log.info("Compilation with id '{}' deleted", compId);
    }

    @Override
    @Transactional
    public CompilationDto update(long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compRepository.findById(compId).orElseThrow();
        compilation.setEvents(findEventsForComp(newCompilationDto));
        if (newCompilationDto.getPinned() != null) compilation.setPinned(newCompilationDto.getPinned());
        if (newCompilationDto.getTitle() != null) compilation.setTitle(newCompilationDto.getTitle());
        log.info("Compilation with id '{}' updated", compilation.getId());
        compRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto findById(long compId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow();
        log.info("Found compilation with id '{}' and title '{}'", compilation.getId(), compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public Collection<CompilationDto> findAllByParam(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = getPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        Collection<Compilation> compilations;
        if (pinned != null) compilations = compRepository.findAllByPinned(pinned, pageable);
        else compilations = compRepository.findAll(pageable).toList();
        log.info("Found '{}' compilations", compilations.size());
        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    private Set<Event> findEventsForComp(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();
        Set<Long> eventIds = newCompilationDto.getEvents();
        if (eventIds != null) {
            Iterable<Event> currentEvents = eventRepository.findAll(QEvent.event.id.in(eventIds));
            currentEvents.forEach(events::add);
            if (events.size() != eventIds.size())
                throw new NoSuchElementException(String.format(
                        "In dataBase not found all Events for new Compilation with title '%s'",
                        newCompilationDto.getTitle()));
        }
        return events;
    }
}
