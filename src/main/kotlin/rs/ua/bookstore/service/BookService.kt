package rs.ua.bookstore.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rs.ua.bookstore.dto.*
import rs.ua.bookstore.entity.Author
import rs.ua.bookstore.entity.Book
import rs.ua.bookstore.entity.Genre
import rs.ua.bookstore.exception.NotFoundException
import rs.ua.bookstore.repository.BookRepository
import java.util.*

interface BookService {
    fun createBook(bookDto: BookRequestDto): BookDto
    fun getBook(id: UUID): BookDto
    fun getBooks(pageable: Pageable): PagedDto<BookDto>
    fun updateBook(id: UUID, book: BookRequestDto): BookDto
    fun deleteBook(id: UUID)
    fun searchBooks(searchCriteria: String, pageable: Pageable): PagedDto<BookDto>
}

@Service
class BookServiceImpl(private val bookRepository: BookRepository) : BookService {

    @Transactional
    override fun createBook(bookDto: BookRequestDto): BookDto {
        return bookRepository.save(
            Book(
                title = bookDto.title,
                price = bookDto.price,
                authors = bookDto.authors.map { Author(id = it.id, name = it.name) },
                genre = bookDto.genre.let { Genre(id = it.id, name = it.name) }
            )
        ).let { mapToBookDto(it) }
    }

    override fun getBook(id: UUID): BookDto {
        return bookRepository.findById(id).orElseThrow { throw NotFoundException("Book with $id not found.") }.let { mapToBookDto(it) }
    }

    override fun getBooks(pageable: Pageable): PagedDto<BookDto> {
        val pagedBooks = bookRepository.findAll(pageable).map { mapToBookDto(it) }
        return PagedDto(
            pagedBooks.toList(),
            pagedBooks.numberOfElements,
            pagedBooks.totalElements,
            pagedBooks.totalPages,
            pagedBooks.hasNext()
        )
    }

    @Transactional
    override fun updateBook(id: UUID, book: BookRequestDto): BookDto {
        bookRepository.findById(id).orElseThrow { throw NotFoundException("Book with $id not found.") }
        return bookRepository.save(
            Book(
                id = id,
                title = book.title,
                price = book.price,
                authors = book.authors.map { Author(id = it.id, name = it.name) },
                genre = book.genre.let { Genre(id = it.id, name = it.name) })
        ).let { mapToBookDto(it) }
    }

    override fun deleteBook(id: UUID) {
        val toDelete = bookRepository.findById(id).orElseThrow { throw NotFoundException("Book with $id not found.") }
        bookRepository.delete(toDelete)
    }

    override fun searchBooks(searchCriteria: String, pageable: Pageable): PagedDto<BookDto> {
        bookRepository.findByTitleOrAuthorNameOrGenreName(searchCriteria, searchCriteria, searchCriteria, pageable).let { pagedBooks ->
            val mappedBooks = pagedBooks.map { mapToBookDto(it) }
            return PagedDto(
                mappedBooks.toList(),
                mappedBooks.numberOfElements,
                mappedBooks.totalElements,
                mappedBooks.totalPages,
                mappedBooks.hasNext()
            )
        }
    }

    private fun mapToBookDto(book: Book): BookDto {
        return BookDto(
            id = book.id!!,
            title = book.title,
            price = book.price,
            authors = book.authors.map { AuthorDto(it.id!!, it.name) },
            genre = GenreDto(book.genre.id!!, book.genre.name)
        )
    }

}