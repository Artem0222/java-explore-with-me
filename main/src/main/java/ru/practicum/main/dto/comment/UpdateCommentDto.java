package ru.practicum.main.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDto {

    @NotBlank
    @Size(min = 1, max = 2000)
    private String text;
}
