package com.ecernare.libros.dto;

import com.ecernare.libros.domain.Book;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {

    private String nameAuthor;

    private Long id;

    private String title;

    private String isbn;

}
