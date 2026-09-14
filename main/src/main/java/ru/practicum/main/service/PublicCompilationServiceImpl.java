package ru.practicum.main.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.util.PageRequestUtil;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getCompilation(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequestUtil.of(from, size);

        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable).getContent();
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        log.info("Found {} compilations", compilations.size());
        return compilations.stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Компиляция с ид= " + compId + " не найдена"));

        log.info("Returning compilation: {}", compilation);
        return compilationMapper.toDto(compilation);
    }
}
