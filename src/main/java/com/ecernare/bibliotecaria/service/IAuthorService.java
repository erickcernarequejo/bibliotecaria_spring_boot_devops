package com.ecernare.bibliotecaria.service;

import com.ecernare.bibliotecaria.dto.AuthorDTO;
import com.ecernare.bibliotecaria.dto.BookDTO;

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
