package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.exception.ConflictException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<Event> events = new HashSet<>();
        newCompilationDto.getEvents().forEach(eventId -> events.add(eventRepository.findById(eventId).orElseThrow()));
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        try {
            compRepository.saveAndFlush(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("The name for title %s already exists", compilation.getTitle()));
        }
        log.info("Compilation with id '{}' and title '{}' created", compilation.getId(), compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow();
        compRepository.deleteById(compId);
        log.info("Compilation with id '{}' deleted", compId);
    }

    @Override
    @Transactional
    public CompilationDto update(long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compRepository.findById(compId).orElseThrow();
        if (newCompilationDto.getEvents() != null) {
            Set<Event> events = new HashSet<>();
            newCompilationDto.getEvents()
                    .forEach(eventId -> events.add(eventRepository.findById(eventId).orElseThrow()));
            compilation.setEvents(events);
        }
        if (newCompilationDto.getPinned() != null) compilation.setPinned(newCompilationDto.getPinned());
        if (newCompilationDto.getTitle() != null) compilation.setTitle(newCompilationDto.getTitle());
        log.info("Compilation with id '{}' updated", compilation.getId());
        compRepository.saveAndFlush(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto findById(long compId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow();
        log.info("Found compilation with id '{}' and title '{}'", compilation.getId(), compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public Collection<CompilationDto> findAllByParam(boolean pinned, Integer from, Integer size) {
        if (from > 0 && size > 0) from = from / size;
        Collection<Compilation> compilations = compRepository.findAllByPinned(pinned,
                PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "Id")));
        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toSet());
    }
}
