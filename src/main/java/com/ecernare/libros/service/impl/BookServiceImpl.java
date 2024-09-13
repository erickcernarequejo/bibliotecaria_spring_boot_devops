package com.ecernare.libros.service.impl;

import com.ecernare.libros.domain.Author;
import com.ecernare.libros.domain.Book;
import com.ecernare.libros.dto.AuthorDTO;
import com.ecernare.libros.dto.BookDTO;
import com.ecernare.libros.mapper.IAuthorMapper;
import com.ecernare.libros.mapper.IBookMapper;
import com.ecernare.libros.repository.IAuthorRepository;
import com.ecernare.libros.repository.IBookRepository;
import com.ecernare.libros.service.IBookService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Log4j2
public class BookServiceImpl implements IBookService {

    private final IAuthorRepository authorRepository;
    private final IBookRepository bookRepository;
    private final IBookMapper bookMapper;
    private final IAuthorMapper authorMapper;

    public static String MESSAGE_AUTHOR_ID_NULL = "Author id must not be null!";

    @Transactional
    @Override
    public Optional<BookDTO> insertNewBook(Long authorId, BookDTO bookDTO) {
        Optional<Author> authorOptional = authorRepository.findById(authorId);
        if (authorOptional.isEmpty()) {
            return Optional.empty();
        } else {
            Book book = bookMapper.bookDTOToBook(bookDTO);
            authorOptional.get().addBook(book);
            authorRepository.save(authorOptional.get());
            return Optional.ofNullable(bookDTO);
        }
    }
}
