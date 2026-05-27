package com.ecernare.bibliotecaria.service;

import com.ecernare.bibliotecaria.domain.Author;
import com.ecernare.bibliotecaria.domain.Book;
import com.ecernare.bibliotecaria.dto.BookDTO;
import com.ecernare.bibliotecaria.exception.ModelNotFoundException;
import com.ecernare.bibliotecaria.mapper.IAuthorMapper;
import com.ecernare.bibliotecaria.mapper.IBookMapper;
import com.ecernare.bibliotecaria.repository.IAuthorRepository;
import com.ecernare.bibliotecaria.repository.IBookRepository;
import com.ecernare.bibliotecaria.service.impl.BookServiceImpl;
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
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource(value = "/application.properties")
public class BookServiceImplTest {

    @MockBean
    private IAuthorRepository authorRepository;

    @MockBean
    private IBookRepository bookRepository;

    @MockBean
    private IBookMapper bookMapper;

    @MockBean
    private IAuthorMapper authorMapper;

    private BookServiceImpl bookService;

    @Before
    public void setup() {
        bookService = new BookServiceImpl(authorRepository, bookRepository, bookMapper, authorMapper);
    }

    @Test
    public void testInsertNewBook_Success() {
        // GIVEN
        Long authorId = 1L;
        BookDTO bookDTO = new BookDTO();
        Author author = new Author();
        Book book = new Book();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookMapper.bookDTOToBook(bookDTO)).thenReturn(book);

        // WHEN
        Optional<BookDTO> result = bookService.insertNewBook(authorId, bookDTO);

        // THEN
        assertTrue(result.isPresent());
        assertEquals(bookDTO, result.get());

        verify(authorRepository).findById(authorId);
        verify(bookMapper).bookDTOToBook(bookDTO);
        verify(authorRepository).save(author);
    }

    @Test
    public void testInsertNewBook_AuthorNotFound() {
        // GIVEN
        Long authorId = 1L;
        BookDTO bookDTO = new BookDTO();

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // WHEN
        Optional<BookDTO> result = bookService.insertNewBook(authorId, bookDTO);

        // THEN
        assertFalse(result.isPresent());

        verify(authorRepository).findById(authorId);
        verifyNoInteractions(bookMapper);
        verifyNoInteractions(bookRepository);
    }

    @Test
    public void testInsertNewBookToAuthor_Success() {
        // GIVEN
        List<Book> books2 = new ArrayList<>();
        Long authorId = 1L;
        Author author = new Author(1L, "Joan Nimar", "Comedy", 35, books2);
        BookDTO bookDTO1 = new BookDTO();
        BookDTO bookDTO2 = new BookDTO();
        Book book1 = new Book(1L, "A History of Ancient Prague", "978-92-95055-02-5", author);
        Book book2 = new Book(2L, "A History of Ancient Portugal", "978-92-95055-02-6", author);
        List<BookDTO> bookDTOs = Arrays.asList(bookDTO1, bookDTO2);
        List<Book> books = Arrays.asList(book1, book2);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookMapper.bookDTOToBook(bookDTOs.get(0))).thenReturn(book1);
        when(bookMapper.bookDTOToBook(bookDTOs.get(1))).thenReturn(book2);
        when(bookRepository.saveAll(anyList())).thenReturn(books);
        when(bookMapper.bookToBookDTO(books)).thenReturn(bookDTOs);

        // WHEN
        List<BookDTO> result = bookService.insertNewBookToAuthor(authorId, bookDTOs);

        // THEN
        assertNotNull(result);
        assertEquals(bookDTOs, result);

        verify(authorRepository).findById(authorId);
        verify(bookMapper, times(2)).bookDTOToBook(any(BookDTO.class));
        verify(bookRepository).saveAll(books);
        verify(bookMapper).bookToBookDTO(books);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testInsertNewBookToAuthor_AuthorNotFound() {
        // GIVEN
        Long authorId = 1L;
        List<BookDTO> bookDTOs = Arrays.asList(new BookDTO(), new BookDTO());

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // WHEN
        bookService.insertNewBookToAuthor(authorId, bookDTOs);

        // THEN
        verify(authorRepository).findById(authorId);
        verifyNoInteractions(bookMapper);
        verifyNoInteractions(bookRepository);
    }
}
