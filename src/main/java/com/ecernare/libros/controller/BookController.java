package com.ecernare.libros.controller;

import com.ecernare.libros.domain.Book;
import com.ecernare.libros.dto.BookDTO;
import com.ecernare.libros.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

}
