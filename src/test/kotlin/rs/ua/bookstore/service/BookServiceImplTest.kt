package rs.ua.bookstore.service

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import rs.ua.bookstore.dto.AuthorDto
import rs.ua.bookstore.dto.BookDto
import rs.ua.bookstore.dto.BookRequestDto
import rs.ua.bookstore.dto.GenreDto
import rs.ua.bookstore.entity.Author
import rs.ua.bookstore.entity.Book
import rs.ua.bookstore.entity.Genre
import rs.ua.bookstore.exception.NotFoundException
import rs.ua.bookstore.repository.BookRepository
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BookServiceImplTest {

    private lateinit var bookRepository: BookRepository
    private lateinit var bookService: BookService

    private val testId = UUID.randomUUID()
    private val testAuthorId = UUID.randomUUID()
    private val testGenreId = UUID.randomUUID()

    private val testAuthor = Author(id = testAuthorId, name = "Test Author")
    private val testGenre = Genre(id = testGenreId, name = "Test Genre")

    private val testBook = Book(
        id = testId,
        title = "Test Book",
        price = BigDecimal("19.99"),
        authors = listOf(testAuthor),
        genre = testGenre
    )

    private val testAuthorDto = AuthorDto(id = testAuthorId, name = "Test Author")
    private val testGenreDto = GenreDto(id = testGenreId, name = "Test Genre")

    private val testBookRequestDto = BookRequestDto(
        title = "Test Book",
        price = BigDecimal("19.99"),
        authors = listOf(testAuthorDto),
        genre = testGenreDto
    )

    private val testBookDto = BookDto(
        id = testId,
        title = "Test Book",
        price = BigDecimal("19.99"),
        authors = listOf(testAuthorDto),
        genre = testGenreDto
    )

    @BeforeEach
    fun setup() {
        bookRepository = mockk()
        bookService = BookServiceImpl(bookRepository)
    }

    @Test
    fun `createBook should save book and return BookDto`() {
        // Given
        every { bookRepository.save(any<Book>()) } returns testBook

        // When
        val result = bookService.createBook(testBookRequestDto)

        // Then
        assertEquals(testBookDto.id, result.id)
        assertEquals(testBookDto.title, result.title)
        assertEquals(testBookDto.price, result.price)
        assertEquals(testBookDto.authors.size, result.authors.size)
        assertEquals(testBookDto.genre.id, result.genre.id)

        verify(exactly = 1) { bookRepository.save(any<Book>()) }
    }

    @Test
    fun `getBook should return BookDto when book exists`() {
        // Given
        every { bookRepository.findById(testId) } returns Optional.of(testBook)

        // When
        val result = bookService.getBook(testId)

        // Then
        assertEquals(testBookDto.id, result.id)
        assertEquals(testBookDto.title, result.title)
        assertEquals(testBookDto.price, result.price)

        verify(exactly = 1) { bookRepository.findById(testId) }
    }

    @Test
    fun `getBook should throw NotFoundException when book does not exist`() {
        // Given
        every { bookRepository.findById(testId) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<NotFoundException> {
            bookService.getBook(testId)
        }

        assertTrue(exception.message!!.contains(testId.toString()))
        verify(exactly = 1) { bookRepository.findById(testId) }
    }

    @Test
    fun `getBooks should return paged list of BookDto`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val books = listOf(testBook)
        val page = PageImpl(books, pageable, 1)

        every { bookRepository.findAll(pageable) } returns page

        // When
        val result = bookService.getBooks(pageable)

        // Then
        assertEquals(1, result.data.size)
        assertEquals(1, result.numberOfElements)
        assertEquals(1L, result.totalElements)
        assertEquals(1, result.totalPages)
        assertFalse(result.hasNext)

        verify(exactly = 1) { bookRepository.findAll(pageable) }
    }

    @Test
    fun `updateBook should update existing book and return BookDto`() {
        // Given
        every { bookRepository.findById(testId) } returns Optional.of(testBook)
        every { bookRepository.save(any<Book>()) } returns testBook

        // When
        val result = bookService.updateBook(testId, testBookRequestDto)

        // Then
        assertEquals(testBookDto.id, result.id)
        assertEquals(testBookDto.title, result.title)
        assertEquals(testBookDto.price, result.price)

        verify(exactly = 1) { bookRepository.findById(testId) }
        verify(exactly = 1) { bookRepository.save(any<Book>()) }
    }

    @Test
    fun `updateBook should throw NotFoundException when book does not exist`() {
        // Given
        every { bookRepository.findById(testId) } returns Optional.empty()

        // When & Then
        assertThrows<NotFoundException> {
            bookService.updateBook(testId, testBookRequestDto)
        }

        verify(exactly = 1) { bookRepository.findById(testId) }
        verify(exactly = 0) { bookRepository.save(any<Book>()) }
    }

    @Test
    fun `deleteBook should delete existing book`() {
        // Given
        every { bookRepository.findById(testId) } returns Optional.of(testBook)
        every { bookRepository.delete(testBook) } just Runs

        // When
        bookService.deleteBook(testId)

        // Then
        verify(exactly = 1) { bookRepository.findById(testId) }
        verify(exactly = 1) { bookRepository.delete(testBook) }
    }

    @Test
    fun `deleteBook should throw exception when book does not exist`() {
        // Given
        every { bookRepository.findById(testId) } returns Optional.empty()

        // When & Then
        assertThrows<NotFoundException> {
            bookService.deleteBook(testId)
        }

        verify(exactly = 1) { bookRepository.findById(testId) }
        verify(exactly = 0) { bookRepository.delete(any()) }
    }

    @Test
    fun `searchBooks should return paged list of matching books`() {
        // Given
        val searchCriteria = "Test"
        val pageable: Pageable = PageRequest.of(0, 10)
        val books = listOf(testBook)
        val page = PageImpl(books, pageable, 1)

        every {
            bookRepository.findByTitleOrAuthorNameOrGenreName(
                searchCriteria,
                searchCriteria,
                searchCriteria,
                pageable
            )
        } returns page

        // When
        val result = bookService.searchBooks(searchCriteria, pageable)

        // Then
        assertEquals(1, result.data.size)
        assertEquals(1, result.numberOfElements)
        assertEquals(1L, result.totalElements)
        assertEquals(1, result.totalPages)
        assertFalse(result.hasNext)

        verify(exactly = 1) {
            bookRepository.findByTitleOrAuthorNameOrGenreName(
                searchCriteria,
                searchCriteria,
                searchCriteria,
                pageable
            )
        }
    }

    @Test
    fun `searchBooks should return empty paged list when no matches found`() {
        // Given
        val searchCriteria = "NonExistent"
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl<Book>(emptyList(), pageable, 0)

        every {
            bookRepository.findByTitleOrAuthorNameOrGenreName(
                searchCriteria,
                searchCriteria,
                searchCriteria,
                pageable
            )
        } returns page

        // When
        val result = bookService.searchBooks(searchCriteria, pageable)

        // Then
        assertEquals(0, result.data.size)
        assertEquals(0, result.numberOfElements)
        assertEquals(0L, result.totalElements)
        assertFalse(result.hasNext)

        verify(exactly = 1) {
            bookRepository.findByTitleOrAuthorNameOrGenreName(
                searchCriteria,
                searchCriteria,
                searchCriteria,
                pageable
            )
        }
    }
}