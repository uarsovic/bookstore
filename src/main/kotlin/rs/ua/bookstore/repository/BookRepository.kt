package rs.ua.bookstore.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import rs.ua.bookstore.entity.Book
import java.util.*

@Repository
interface BookRepository : JpaRepository<Book, UUID> {

    @Query(
        "SELECT DISTINCT b FROM Book b " +
                "JOIN b.genre g " +
                "JOIN b.authors a " +
                "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
                "OR LOWER(g.name) LIKE LOWER(CONCAT('%', :genreName, '%')) " +
                "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))"
    )
    fun findByTitleOrAuthorNameOrGenreName(
        title: String,
        authorName: String,
        genreName: String,
        pageable: Pageable
    ): Page<Book>
}