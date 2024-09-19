package com.ecernare.bibliotecaria.repository;

import com.ecernare.bibliotecaria.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT COUNT(b) FROM Book b WHERE b.id IN :bookIds AND b.author.id = :authorId")
    Long countByAuthorIdAndBookIds(Long authorId, List<Long> bookIds);

}
