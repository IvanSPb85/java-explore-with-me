package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compRepository;
    private final EventRepository eventRepository;

    @Override
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
    public void delete(Long compId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow();
        compRepository.deleteById(compId);
        log.info("Compilation with id '{}' deleted", compId);
    }

    @Override
    public CompilationDto update(long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compRepository.findById(compId).orElseThrow();
        return create(newCompilationDto);
    }
}
