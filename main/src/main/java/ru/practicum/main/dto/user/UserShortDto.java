package ru.practicum.main.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {

    private Long id;
    private String name;
}
