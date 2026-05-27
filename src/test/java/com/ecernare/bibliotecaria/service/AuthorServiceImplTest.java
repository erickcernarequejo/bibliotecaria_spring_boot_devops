package com.ecernare.bibliotecaria.service;

import com.ecernare.bibliotecaria.domain.Author;
import com.ecernare.bibliotecaria.domain.Book;
import com.ecernare.bibliotecaria.dto.AuthorDTO;
import com.ecernare.bibliotecaria.dto.BookDTO;
import com.ecernare.bibliotecaria.exception.ModelExistsException;
import com.ecernare.bibliotecaria.exception.ModelNotFoundException;
import com.ecernare.bibliotecaria.exception.UnauthorizedException;
import com.ecernare.bibliotecaria.mapper.IAuthorMapper;
import com.ecernare.bibliotecaria.mapper.IBookMapper;
import com.ecernare.bibliotecaria.repository.IAuthorRepository;
import com.ecernare.bibliotecaria.repository.IBookRepository;
import com.ecernare.bibliotecaria.service.impl.AuthorServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource(value = "/application.properties")
public class AuthorServiceImplTest {

    @MockBean
    private IAuthorRepository authorRepository;

    @MockBean
    private IAuthorMapper authorMapper;

    @MockBean
    private IBookMapper bookMapper;

    @MockBean
    private IBookRepository bookRepository;

    private AuthorServiceImpl authorService;

    @Before
    public void setup() {
        authorService = new AuthorServiceImpl(authorRepository, authorMapper, bookMapper, bookRepository);
    }

    @Test
    public void testGetAuthorById() {
        // GIVEN
        Long id = 1L;
        Author author = new Author();
        AuthorDTO authorDTO = new AuthorDTO();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.authorToAuthorDTO(author)).thenReturn(authorDTO);

        // WHEN
        AuthorDTO optionalAuthorDTO = authorService.getAuthorById(1L);

        // THEN
        assertSame(authorDTO, optionalAuthorDTO);

        verify(authorRepository).findById(id);
        verify(authorMapper).authorToAuthorDTO(author);

    }

    @Test
    public void testGetAuthorByIdNotFound() {
        // GIVEN
        Long id = 1L;
        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ModelNotFoundException.class, () -> authorService.getAuthorById(id));
        verify(authorRepository).findById(id);
    }

    @Test
    public void testGetAuthors() {
        // GIVEN
        List<Author> authors = Arrays.asList(new Author(), new Author());
        List<AuthorDTO> authorDTOs = Arrays.asList(new AuthorDTO(), new AuthorDTO());

        when(authorRepository.findAll()).thenReturn(authors);
        when(authorMapper.authorToAuthorDTOList(authors)).thenReturn(authorDTOs);

        // WHEN
        List<AuthorDTO> result = authorService.getAuthors();

        // THEN
        assertSame(authorDTOs, result);
        verify(authorRepository).findAll();
        verify(authorMapper).authorToAuthorDTOList(authors);
    }

    @Test
    public void testCreateAuthor() {
        // GIVEN
        Long id = 1L;
        Author author = new Author();
        author.setId(id);
        AuthorDTO authorDTO = new AuthorDTO();

        Author attachedAuthor = new Author();
        AuthorDTO attachedAuthorDTO = new AuthorDTO();

        when(authorMapper.authorDTOToAuthor(authorDTO)).thenReturn(author);
        when(authorRepository.existsById(id)).thenReturn(false);
        when(authorRepository.save(author)).thenReturn(attachedAuthor);
        when(authorMapper.authorToAuthorDTO(attachedAuthor)).thenReturn(attachedAuthorDTO);

        // WHEN
        AuthorDTO authorRet = authorService.createAuthor(authorDTO);

        // THEN
        assertSame(attachedAuthorDTO, authorRet);

        verify(authorMapper).authorDTOToAuthor(authorDTO);
        verify(authorRepository).existsById(id);
        verify(authorRepository).save(author);
        verify(authorMapper).authorToAuthorDTO(attachedAuthor);

    }

    @Test
    public void testCreateAuthorAlreadyExists() {
        // GIVEN
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(1L);
        Author author = new Author();
        author.setId(1L);

        when(authorMapper.authorDTOToAuthor(authorDTO)).thenReturn(author);
        when(authorRepository.existsById(1L)).thenReturn(true);

        // WHEN & THEN
        assertThrows(ModelExistsException.class, () -> authorService.createAuthor(authorDTO));
        verify(authorRepository).existsById(1L);
    }

    @Test
    public void testInsertNewBook() {
        // GIVEN
        Long authorId = 1L;
        Author author = new Author();
        List<BookDTO> bookDTOs = Arrays.asList(new BookDTO(), new BookDTO());
        List<Book> books = Arrays.asList(new Book(), new Book());
        Author updatedAuthor = new Author();
        AuthorDTO updatedAuthorDTO = new AuthorDTO();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookMapper.bookDTOToBook(any(BookDTO.class))).thenReturn(books.get(0), books.get(1));
        when(authorRepository.save(author)).thenReturn(updatedAuthor);
        when(authorMapper.authorToAuthorDTO(updatedAuthor)).thenReturn(updatedAuthorDTO);

        // WHEN
        Optional<AuthorDTO> result = authorService.insertNewBook(authorId, bookDTOs);

        // THEN
        assertTrue(result.isPresent());
        assertSame(updatedAuthorDTO, result.get());
        verify(authorRepository).findById(authorId);
        verify(bookMapper, times(2)).bookDTOToBook(any(BookDTO.class));
        verify(bookRepository, times(2)).save(any(Book.class));
        verify(authorRepository).save(author);
        verify(authorMapper).authorToAuthorDTO(updatedAuthor);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testInsertNewBookNotFoundException() {
        // GIVEN
        Long authorId = 1L;
        List<BookDTO> bookDTOs = Arrays.asList(new BookDTO(), new BookDTO());

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        try {
            authorService.insertNewBook(authorId, bookDTOs);
        } catch (ModelNotFoundException ex) {
            verify(authorRepository).findById(authorId);
            throw ex;
        }
    }

    @Test
    public void testUpdateAuthor() {
        // GIVEN
        Long authorId = 1L;
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(authorId);
        Author existingAuthor = new Author();
        Author updatedAuthor = new Author();
        AuthorDTO updatedAuthorDTO = new AuthorDTO();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(existingAuthor)).thenReturn(updatedAuthor);
        when(authorMapper.authorToAuthorDTO(updatedAuthor)).thenReturn(updatedAuthorDTO);

        // WHEN
        Optional<AuthorDTO> result = authorService.updateAuthor(authorDTO);

        // THEN
        assertTrue(result.isPresent());
        assertSame(updatedAuthorDTO, result.get());
        verify(authorRepository).findById(authorId);
        verify(authorMapper).updateAuthorFromAuthorDTO(authorDTO, existingAuthor);
        verify(authorRepository).save(existingAuthor);
        verify(authorMapper).authorToAuthorDTO(updatedAuthor);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testUpdateAuthorNotFoundException() {
        // GIVEN
        Long authorId = 1L;
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(authorId);
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        try {
            // WHEN
            authorService.updateAuthor(authorDTO);
        } catch (ModelNotFoundException ex) {
            // THEN
            verify(authorRepository).findById(authorId);
            throw ex;
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateAuthorThrowsUnauthorizedExceptionForInvalidBooks() {
        // GIVEN
        Long authorId = 1L;
        List<BookDTO> booksDTO = List.of(new BookDTO(1L, "A History of Ancient Prague", "978-92-95055-02-5"),
                new BookDTO(2L, "A History of Ancient Portugal", "978-92-95055-02-6"));
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(authorId);
        authorDTO.setBooksDTO(booksDTO);

        Author existingAuthor = new Author();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(bookRepository.countByAuthorIdAndBookIds(authorId, List.of(1L, 2L))).thenReturn(1L);

        try {
            // WHEN
            authorService.updateAuthor(authorDTO);
        } catch (UnauthorizedException ex) {
            // THEN
            verify(authorRepository).findById(authorId);
            verify(bookRepository).countByAuthorIdAndBookIds(authorId, List.of(1L, 2L));
            throw ex;
        }
    }

    @Test
    public void testDeleteAuthorById() {
        // GIVEN
        Long authorId = 1L;
        Author author = new Author();
        author.setId(authorId);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        // WHEN
        authorService.deleteAuthorById(authorId);

        // THEN
        verify(authorRepository).findById(author.getId());
        verify(authorRepository).deleteById(author.getId());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteAuthorByIdNotFoundException() {
        // GIVEN
        Long authorId = 1L;
        Author author = new Author();
        author.setId(authorId);

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        try {
            authorService.deleteAuthorById(authorId);
        } catch (ModelNotFoundException ex) {
            verify(authorRepository).findById(authorId);
            throw ex;
        }
    }

    @Test
    public void testDeleteLastBook() {
        // GIVEN
        Long authorId = 1L;
        Author author = new Author();
        author.setId(authorId);
        Book book1 = new Book();
        Book book2 = new Book();
        author.addBook(book1);
        author.addBook(book2);

        when(authorRepository.fetchByName("Gabriel García Márquez")).thenReturn(author);

        // WHEN
        authorService.deleteLastBook(author.getId());

        // THEN
        verify(authorRepository).fetchByName("Gabriel García Márquez");
        assertEquals(1, author.getBooks().size());
        assertSame(book1, author.getBooks().getFirst());
    }

    @Test
    public void testDeleteLastBookEmptyList() {
        // GIVEN
        Long authorId = 1L;
        Author author = new Author();
        author.setId(authorId);
        author.setBooks(new ArrayList<>());

        when(authorRepository.fetchByName("Gabriel García Márquez")).thenReturn(author);

        // WHEN & THEN
        assertThrows(ModelNotFoundException.class, () -> authorService.deleteLastBook(author.getId()));
        verify(authorRepository).fetchByName("Gabriel García Márquez");
        verifyNoInteractions(bookRepository);
        verifyNoMoreInteractions(authorRepository);
    }

}
