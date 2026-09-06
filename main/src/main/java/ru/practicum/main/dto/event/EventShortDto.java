package ru.practicum.main.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.user.UserShortDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}