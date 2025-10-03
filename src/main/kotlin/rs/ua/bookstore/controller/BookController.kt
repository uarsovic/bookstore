package rs.ua.bookstore.controller

import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import rs.ua.bookstore.config.Roles.ADMIN
import rs.ua.bookstore.config.Roles.USER
import rs.ua.bookstore.dto.BookDto
import rs.ua.bookstore.dto.BookRequestDto
import rs.ua.bookstore.dto.PagedDto
import rs.ua.bookstore.service.BookService
import java.util.*

@RestController
@RequestMapping("/book")
@Validated
class BookController(private val bookService: BookService) {

    @PostMapping
    @RolesAllowed(ADMIN)
    fun createNewBook(@RequestBody @Valid bookRequestDto: BookRequestDto): BookDto = bookService.createBook(bookRequestDto)

    @PutMapping("/{id}")
    @RolesAllowed(ADMIN)
    fun updateBook(@PathVariable @Valid id: UUID, @Valid @RequestBody bookRequestDto: BookRequestDto): BookDto =
        bookService.updateBook(id, bookRequestDto)

    @DeleteMapping("/{id}")
    @RolesAllowed(ADMIN)
    fun deleteBook(@PathVariable @Valid id: UUID) = bookService.deleteBook(id)

    @GetMapping("/{id}")
    @RolesAllowed(USER, ADMIN)
    fun getBook(@PathVariable @Valid id: UUID): BookDto = bookService.getBook(id)

    @GetMapping
    @RolesAllowed(USER, ADMIN)
    fun getBooks(pageable: Pageable): PagedDto<BookDto> = bookService.getBooks(pageable)

    @GetMapping("/search")
    @RolesAllowed(USER, ADMIN)
    fun searchBooks(@RequestParam @Size(min = 3) criteria: String, pageable: Pageable): PagedDto<BookDto> =
        bookService.searchBooks(criteria, pageable)
}