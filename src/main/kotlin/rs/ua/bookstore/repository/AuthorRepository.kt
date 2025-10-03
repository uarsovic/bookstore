package rs.ua.bookstore.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import rs.ua.bookstore.entity.Author
import java.util.*

@Repository
interface AuthorRepository : JpaRepository<Author, UUID>