package com.ecernare.bibliotecaria.service.impl;

import com.ecernare.bibliotecaria.domain.Author;
import com.ecernare.bibliotecaria.domain.Book;
import com.ecernare.bibliotecaria.dto.BookDTO;
import com.ecernare.bibliotecaria.mapper.IAuthorMapper;
import com.ecernare.bibliotecaria.mapper.IBookMapper;
import com.ecernare.bibliotecaria.repository.IAuthorRepository;
import com.ecernare.bibliotecaria.repository.IBookRepository;
import com.ecernare.bibliotecaria.service.IBookService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
