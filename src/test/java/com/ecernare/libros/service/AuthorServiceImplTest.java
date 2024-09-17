package com.ecernare.libros.service;

import com.ecernare.libros.domain.Author;
import com.ecernare.libros.dto.AuthorDTO;
import com.ecernare.libros.mapper.IAuthorMapper;
import com.ecernare.libros.mapper.IBookMapper;
import com.ecernare.libros.repository.IAuthorRepository;
import com.ecernare.libros.repository.IBookRepository;
import com.ecernare.libros.service.impl.AuthorServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
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
    public void testInsertAuthor() {
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

}
