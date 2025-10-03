package rs.ua.bookstore.helper

import rs.ua.bookstore.dto.AuthorDto
import rs.ua.bookstore.dto.BookRequestDto
import rs.ua.bookstore.dto.GenreDto
import rs.ua.bookstore.entity.Author
import rs.ua.bookstore.entity.Book
import rs.ua.bookstore.entity.Genre
import java.math.BigDecimal
import java.util.*

object TestDataBuilder {

    fun buildAuthor(
        id: UUID? = null,
        name: String = "Test Author"
    ) = Author(id = id, name = name)

    fun buildGenre(
        id: UUID? = null,
        name: String = "Test Genre"
    ) = Genre(id = id, name = name)

    fun buildBook(
        id: UUID? = null,
        title: String = "Test Book",
        price: BigDecimal = BigDecimal("19.99"),
        authors: List<Author> = emptyList(),
        genre: Genre
    ) = Book(
        id = id,
        title = title,
        price = price,
        authors = authors,
        genre = genre
    )

    fun buildAuthorDto(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Author"
    ) = AuthorDto(id = id, name = name)

    fun buildGenreDto(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Genre"
    ) = GenreDto(id = id, name = name)

    fun buildBookRequestDto(
        title: String = "Test Book",
        price: BigDecimal = BigDecimal("19.99"),
        authors: List<AuthorDto> = emptyList(),
        genre: GenreDto
    ) = BookRequestDto(
        title = title,
        price = price,
        authors = authors,
        genre = genre
    )
}