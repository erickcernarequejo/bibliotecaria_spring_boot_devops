package com.ecernare.bibliotecaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    @Schema(title = "Identification code", example = "1")
    @Digits(integer = 3, fraction = 0, message = "Max 5 digits, book identification code")
    @PositiveOrZero(message = "Identification code not be negative")
    private Long id;

    @Schema(title = "Book title", example = "A History of Ancient Prague")
    @Size(max = 80, message = "Max 80 characters, author title")
    @NotBlank
    private String title;

    @Schema(title = "International Standard Book Number", example = "978-92-95055-02-5")
    @Size(max = 17, message = "Max 17 characters, International Standard Book Number")
    @NotBlank
    private String isbn;

}
