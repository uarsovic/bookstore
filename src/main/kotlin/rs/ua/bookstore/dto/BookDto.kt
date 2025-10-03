package rs.ua.bookstore.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.*

data class BookDto(
    @param:Valid val id: UUID,
    val title: String,
    val price: BigDecimal,
    val authors: List<AuthorDto>,
    val genre: GenreDto
)

data class BookRequestDto(
    @param:Size(min = 3) val title: String,
    @param:Digits(integer = 4, fraction = 2) val price: BigDecimal,
    val authors: List<AuthorDto>,
    val genre: GenreDto
)