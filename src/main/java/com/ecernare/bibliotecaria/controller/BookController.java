package com.ecernare.bibliotecaria.controller;

import com.ecernare.bibliotecaria.dto.AuthorDTO;
import com.ecernare.bibliotecaria.dto.BookDTO;
import com.ecernare.bibliotecaria.service.IBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final IBookService bookstoreService;

    @PostMapping(value = "/{id}")
    public ResponseEntity<BookDTO> insertNewBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        Optional<BookDTO> bookResponseDTO = bookstoreService.insertNewBook(id, bookDTO);
        if (bookResponseDTO.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author with id " + id + " not found.");
        }
        return new ResponseEntity<>(bookResponseDTO.get(), HttpStatus.CREATED);
    }

    @Operation(summary = "Insert new book to an existing author", description = "Insert new book to an existing author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping(value = "/author/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookDTO> insertNewBookToAuthor(@PathVariable("authorId") Long authorId, @RequestBody List<BookDTO> booksDTO) {
        List<BookDTO> author = bookstoreService.insertNewBookToAuthor(authorId, booksDTO);

        if (author.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author with id " + authorId + " not found.");
        } else {
            return new ResponseEntity<>(author.get(0), HttpStatus.OK);
        }

    }

}
