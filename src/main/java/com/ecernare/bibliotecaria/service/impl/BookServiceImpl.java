package com.ecernare.bibliotecaria.service.impl;

import com.ecernare.bibliotecaria.domain.Author;
import com.ecernare.bibliotecaria.domain.Book;
import com.ecernare.bibliotecaria.dto.AuthorDTO;
import com.ecernare.bibliotecaria.dto.BookDTO;
import com.ecernare.bibliotecaria.exception.ModelNotFoundException;
import com.ecernare.bibliotecaria.mapper.IAuthorMapper;
import com.ecernare.bibliotecaria.mapper.IBookMapper;
import com.ecernare.bibliotecaria.repository.IAuthorRepository;
import com.ecernare.bibliotecaria.repository.IBookRepository;
import com.ecernare.bibliotecaria.service.IBookService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
@Log4j2
public class BookServiceImpl implements IBookService {

    public static final String MESSAGE_AUTHOR_NOT_FOUND = "Author id \"%s\" not found";

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
            return Optional.ofNullable(bookMapper.bookToBookDTO(book));
        }
    }

    /**
     * Inserts new books to an existing author in the system.
     * The method maps a list of {@code BookDTO} objects to {@code Book} entities, associates them with the specified author,
     * and saves them to the database. Throws a {@code ModelNotFoundException} if the author does not exist.
     *
     * @param authorId the ID of the author to whom new books will be added
     * @param booksDTO the list of book data transfer objects representing the books to be added
     * @return a list of {@code BookDTO} objects representing the newly added books associated with the author
     * @throws ModelNotFoundException if the author with the specified ID is not found
     */
    @Transactional
    @Override
    public List<BookDTO> insertNewBookToAuthor(Long authorId, List<BookDTO> booksDTO) {
        log.debug("Start of the method insertNewBook with author id: {}", authorId);
        Optional<Author> authorOptional = authorRepository.findById(authorId);

        if (authorOptional.isEmpty()) {
            throw new ModelNotFoundException(String.format(MESSAGE_AUTHOR_NOT_FOUND, authorId));
        } else {
            Author author = authorOptional.get();

            List<Book> books = booksDTO.stream()
                    .map(bookMapper::bookDTOToBook)
                    .peek(book -> book.setAuthor(author))
                    .collect(Collectors.toList());

            List<Book> attachedBooks = bookRepository.saveAll(books);

            return bookMapper.bookToBookDTO(attachedBooks);

        }
    }
}
