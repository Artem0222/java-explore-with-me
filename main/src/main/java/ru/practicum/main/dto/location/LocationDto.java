package ru.practicum.main.dto.location;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}
