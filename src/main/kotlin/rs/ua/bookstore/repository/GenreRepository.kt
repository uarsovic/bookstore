package rs.ua.bookstore.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import rs.ua.bookstore.entity.Genre
import java.util.*

@Repository
interface GenreRepository : JpaRepository<Genre, UUID>