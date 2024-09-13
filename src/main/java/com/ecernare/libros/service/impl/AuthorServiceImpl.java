package com.ecernare.libros.service.impl;

import com.ecernare.libros.domain.Author;
import com.ecernare.libros.domain.Book;
import com.ecernare.libros.dto.AuthorDTO;
import com.ecernare.libros.dto.BookDTO;
import com.ecernare.libros.exception.ModelExistsException;
import com.ecernare.libros.exception.ModelNotFoundException;
import com.ecernare.libros.exception.UnauthorizedException;
import com.ecernare.libros.mapper.IAuthorMapper;
import com.ecernare.libros.mapper.IBookMapper;
import com.ecernare.libros.repository.IAuthorRepository;
import com.ecernare.libros.repository.IBookRepository;
import com.ecernare.libros.service.IAuthorService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Log4j2
public class AuthorServiceImpl implements IAuthorService {

    public static final String MESSAGE_AUTHOR_NOT_FOUND = "Author id \"%s\" not found";
    public static final String MESSAGE_AUTHOR_EXISTS = "Author id \"%s\" exists";
    public static final String MESSAGE_AUTHOR_UNAUTHORIZED = "One or more books do not belong to the author with id :: \"%s\"";

    private final IAuthorRepository authorRepository;
    private final IAuthorMapper authorMapper;
    private final IBookMapper bookMapper;
    private final IBookRepository bookRepository;

    @Override
    public AuthorDTO getAuthorById(Long id) {
        log.debug("Start of the method getAuthorById {}", id);
        Optional<Author> authorOptional = authorRepository.findById(id);
        if (authorOptional.isEmpty()) {
            throw new ModelNotFoundException(String.format(MESSAGE_AUTHOR_NOT_FOUND, id));
        } else {
            return authorMapper.authorToAuthorDTO(authorOptional.get());
        }
    }

    @Override
    public List<AuthorDTO> getAuthors() {
        log.debug("Start of the method getAuthors");
        return authorMapper.authorToAuthorDTOList(authorRepository.findAll());
    }

    @Transactional
    @Override
    public AuthorDTO insert(AuthorDTO authorDTO) {
        log.debug("Start of the method insert");
        Author author = authorMapper.authorDTOToAuthor(authorDTO);
        Long id = author.getId();

        if (id != null && authorRepository.existsById(id)) {
            throw new ModelExistsException(String.format(MESSAGE_AUTHOR_EXISTS, id));
        } else {
            Author attachedAuthor = authorRepository.save(author);
            return authorMapper.authorToAuthorDTO(attachedAuthor);
        }

    }

    @Transactional
    @Override
    public Optional<AuthorDTO> insertNewBook(Long id, List<BookDTO> booksDTO) {
        log.debug("Start of the method insertNewBook with author id: {}", id);
        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ModelNotFoundException(String.format(MESSAGE_AUTHOR_NOT_FOUND, id));
        } else {
            Author author = authorOptional.get();
            for (BookDTO bookDTO : booksDTO) {
                Book book = bookMapper.bookDTOToBook(bookDTO);
                author.addBook(book);
                bookRepository.save(book);
            }
            Author attachedAuthor = authorRepository.save(authorOptional.get());
            return Optional.ofNullable(authorMapper.authorToAuthorDTO(attachedAuthor));
        }

    }

    @Override
    public Optional<AuthorDTO> update(AuthorDTO authorDTO) {
        log.debug("Start of the method update with author id: {}", authorDTO.getId());
        Long id = authorDTO.getId();

        Assert.notNull(id, MESSAGE_AUTHOR_NOT_FOUND);

        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ModelNotFoundException(String.format(MESSAGE_AUTHOR_NOT_FOUND, id));
        } else {
            List<Long> bookIds = authorDTO.getBooksDTO().stream()
                    .map(BookDTO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!bookIds.isEmpty()) {
                Long count = bookRepository.countByAuthorIdAndBookIds(authorDTO.getId(), bookIds);
                if (count != bookIds.size()) {
                    throw new UnauthorizedException(String.format(MESSAGE_AUTHOR_UNAUTHORIZED, id));
                }
            }

            authorMapper.updateAuthorFromAuthorDTO(authorDTO, authorOptional.get());
            Author attachedAuthor = authorRepository.save(authorOptional.get());
            return Optional.ofNullable(authorMapper.authorToAuthorDTO(attachedAuthor));
        }
    }

    @Override
    public void delete(Long id) {
        log.debug("Start of the method delete with author id: {}", id);

        Assert.notNull(id, MESSAGE_AUTHOR_NOT_FOUND);

        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ModelNotFoundException(String.format(MESSAGE_AUTHOR_NOT_FOUND, id));
        }

        authorRepository.deleteById(authorOptional.get().getId());
    }

    @Transactional
    @Override
    public void deleteLastBook(Long id) {
        Author author = authorRepository.fetchByName("Gabriel García Márquez");
        List<Book> books = author.getBooks();
        if (books.isEmpty()) {
            throw new ModelNotFoundException(String.format(MESSAGE_AUTHOR_NOT_FOUND, id));
        }
        author.removeBook(books.getLast());
    }
}
