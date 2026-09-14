package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.main.service.AdminCompilationService;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("POST /admin/compilations: {}", newCompilationDto);
        return adminCompilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest updateRequest
    ) {
        log.info("PATCH /admin/compilations/{}: {}", compId, updateRequest);
        return adminCompilationService.updateCompilation(compId, updateRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("DELETE /admin/compilations/{}", compId);
        adminCompilationService.deleteCompilation(compId);
    }
}
