package ru.practicum.main.service;

import ru.practicum.main.dto.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilation(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(Long compId);
}
