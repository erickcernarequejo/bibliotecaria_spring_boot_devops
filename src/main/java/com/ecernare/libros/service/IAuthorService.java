package com.ecernare.libros.service;

import com.ecernare.libros.dto.AuthorDTO;
import com.ecernare.libros.dto.BookDTO;

import java.util.List;
import java.util.Optional;

public interface IAuthorService {

    AuthorDTO getAuthorById(Long id);

    List<AuthorDTO> getAuthors();

    AuthorDTO createAuthor(AuthorDTO authorDTO);

    Optional<AuthorDTO> insertNewBook(Long id, List<BookDTO> booksDTO);

    Optional<AuthorDTO> updateAuthor(AuthorDTO authorDTO);

    void deleteAuthorById(Long id);

    void deleteLastBook(Long id);

}
