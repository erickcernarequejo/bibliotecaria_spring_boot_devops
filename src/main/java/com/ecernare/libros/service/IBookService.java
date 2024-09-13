package com.ecernare.libros.service;

import com.ecernare.libros.dto.BookDTO;

import java.util.List;
import java.util.Optional;

public interface IBookService {

    Optional<BookDTO> insertNewBook(Long authorId, BookDTO bookDTO);
}
