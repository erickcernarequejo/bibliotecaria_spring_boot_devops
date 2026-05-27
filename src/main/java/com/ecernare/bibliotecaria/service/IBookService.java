package com.ecernare.bibliotecaria.service;

import com.ecernare.bibliotecaria.dto.AuthorDTO;
import com.ecernare.bibliotecaria.dto.BookDTO;

import java.util.List;
import java.util.Optional;

public interface IBookService {

    Optional<BookDTO> insertNewBook(Long authorId, BookDTO bookDTO);

    List<BookDTO> insertNewBookToAuthor(Long authorId, List<BookDTO> booksDTO);
}
