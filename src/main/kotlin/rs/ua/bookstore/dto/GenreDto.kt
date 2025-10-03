package rs.ua.bookstore.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.util.*

data class GenreDto(@param:Valid val id: UUID, @param:Size(min = 3) val name: String)