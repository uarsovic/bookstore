package rs.ua.bookstore.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.*
import rs.ua.bookstore.config.BaseIntegrationTest
import rs.ua.bookstore.config.TestSecurityConfig
import rs.ua.bookstore.dto.AuthorDto
import rs.ua.bookstore.dto.BookRequestDto
import rs.ua.bookstore.dto.GenreDto
import rs.ua.bookstore.entity.Author
import rs.ua.bookstore.entity.Book
import rs.ua.bookstore.entity.Genre
import rs.ua.bookstore.helper.TestDataBuilder
import rs.ua.bookstore.repository.AuthorRepository
import rs.ua.bookstore.repository.BookRepository
import rs.ua.bookstore.repository.GenreRepository
import java.math.BigDecimal
import java.util.*

@AutoConfigureMockMvc
@Import(TestSecurityConfig::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BookControllerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var genreRepository: GenreRepository

    private lateinit var testAuthor: Author
    private lateinit var testGenre: Genre
    private lateinit var testBook: Book

    @BeforeEach
    fun setup() {
        // Clean up
        bookRepository.deleteAll()
        authorRepository.deleteAll()
        genreRepository.deleteAll()

        // Create test data
        testAuthor = authorRepository.save(TestDataBuilder.buildAuthor(name = "John Doe"))
        testGenre = genreRepository.save(TestDataBuilder.buildGenre(name = "Fiction"))
        testBook = bookRepository.save(
            TestDataBuilder.buildBook(
                title = "The Great Book",
                price = BigDecimal("29.99"),
                authors = listOf(testAuthor),
                genre = testGenre
            )
        )
    }

    @Nested
    @DisplayName("POST /book - Create Book")
    inner class CreateBookTests {

        @Test
        fun `should create book with admin role`() {
            // Given
            val newAuthor = authorRepository.save(TestDataBuilder.buildAuthor(name = "Jane Smith"))
            val bookRequest = TestDataBuilder.buildBookRequestDto(
                title = "New Amazing Book",
                price = BigDecimal("39.99"),
                authors = listOf(AuthorDto(newAuthor.id!!, newAuthor.name)),
                genre = GenreDto(testGenre.id!!, testGenre.name)
            )

            // When & Then
            mockMvc.post("/book") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(bookRequest)
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.title") { value("New Amazing Book") }
                jsonPath("$.price") { value(39.99) }
                jsonPath("$.authors[0].name") { value("Jane Smith") }
                jsonPath("$.genre.name") { value("Fiction") }
            }
        }

        @Test
        fun `should return 403 when user role tries to create book`() {
            val bookRequest = TestDataBuilder.buildBookRequestDto(
                title = "Unauthorized Book",
                price = BigDecimal("19.99"),
                authors = listOf(AuthorDto(testAuthor.id!!, testAuthor.name)),
                genre = GenreDto(testGenre.id!!, testGenre.name)
            )

            mockMvc.post("/book") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(bookRequest)
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `should return 400 when book data is invalid`() {
            val invalidRequest = """
                {
                    "title": "",
                    "price": -10.00,
                    "authors": [],
                    "genre": null
                }
            """

            mockMvc.post("/book") {
                contentType = MediaType.APPLICATION_JSON
                content = invalidRequest
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }

    @Nested
    @DisplayName("GET /book/{id} - Get Book by ID")
    inner class GetBookTests {

        @Test
        fun `should get book with user role`() {
            mockMvc.get("/book/${testBook.id}") {
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.id") { value(testBook.id.toString()) }
                jsonPath("$.title") { value("The Great Book") }
                jsonPath("$.price") { value(29.99) }
            }
        }

        @Test
        fun `should get book with admin role`() {
            mockMvc.get("/book/${testBook.id}") {
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.id") { value(testBook.id.toString()) }
            }
        }

        @Test
        fun `should return 404 when book not found`() {
            val nonExistentId = UUID.randomUUID()

            mockMvc.get("/book/$nonExistentId") {
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `should return 401 when not authenticated`() {
            mockMvc.get("/book/${testBook.id}")
                .andExpect {
                    status { isUnauthorized() }
                }
        }
    }

    @Nested
    @DisplayName("GET /book - Get All Books")
    inner class GetAllBooksTests {

        @Test
        fun `should get paginated books`() {
            mockMvc.get("/book") {
                param("page", "0")
                param("size", "10")
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data") { isArray() }
                jsonPath("$.data[0].id") { value(testBook.id.toString()) }
                jsonPath("$.numberOfElements") { value(1) }
                jsonPath("$.totalElements") { value(1) }
            }
        }

        @Test
        fun `should handle pagination correctly with multiple books`() {
            // Create additional books
            repeat(15) { index ->
                bookRepository.save(
                    TestDataBuilder.buildBook(
                        title = "Book $index",
                        price = BigDecimal("${10 + index}.99"),
                        authors = listOf(testAuthor),
                        genre = testGenre
                    )
                )
            }

            // First page
            mockMvc.get("/book") {
                param("page", "0")
                param("size", "10")
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.numberOfElements") { value(10) }
                jsonPath("$.totalElements") { value(16) }
                jsonPath("$.totalPages") { value(2) }
                jsonPath("$.hasNext") { value(true) }
            }

            // Second page
            mockMvc.get("/book") {
                param("page", "1")
                param("size", "10")
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.numberOfElements") { value(6) }
                jsonPath("$.hasNext") { value(false) }
            }
        }
    }

    @Nested
    @DisplayName("PUT /book/{id} - Update Book")
    inner class UpdateBookTests {

        @Test
        fun `should update book with admin role`() {
            val updateRequest = BookRequestDto(
                title = "Updated Title",
                price = BigDecimal("49.99"),
                authors = listOf(AuthorDto(testAuthor.id!!, testAuthor.name)),
                genre = GenreDto(testGenre.id!!, testGenre.name)
            )

            mockMvc.put("/book/${testBook.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updateRequest)
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.id") { value(testBook.id.toString()) }
                jsonPath("$.title") { value("Updated Title") }
                jsonPath("$.price") { value(49.99) }
            }

            // Verify in database
            val updated = bookRepository.findById(testBook.id!!).get()
            Assertions.assertEquals("Updated Title", updated.title)
            Assertions.assertEquals(BigDecimal("49.99"), updated.price)
        }

        @Test
        fun `should return 403 when user role tries to update book`() {
            val updateRequest = BookRequestDto(
                title = "Unauthorized Update",
                price = BigDecimal("19.99"),
                authors = listOf(AuthorDto(testAuthor.id!!, testAuthor.name)),
                genre = GenreDto(testGenre.id!!, testGenre.name)
            )

            mockMvc.put("/book/${testBook.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updateRequest)
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `should return 404 when updating non-existent book`() {
            val nonExistentId = UUID.randomUUID()
            val updateRequest = BookRequestDto(
                title = "Non-existent Book",
                price = BigDecimal("19.99"),
                authors = listOf(AuthorDto(testAuthor.id!!, testAuthor.name)),
                genre = GenreDto(testGenre.id!!, testGenre.name)
            )

            mockMvc.put("/book/$nonExistentId") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updateRequest)
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
            }.andExpect {
                status { isNotFound() }
            }
        }
    }

    @Nested
    @DisplayName("DELETE /book/{id} - Delete Book")
    inner class DeleteBookTests {

        @Test
        fun `should delete book with admin role`() {
            mockMvc.delete("/book/${testBook.id}") {
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
            }.andExpect {
                status { isOk() }
            }

            // Verify book is deleted
            Assertions.assertFalse(bookRepository.findById(testBook.id!!).isPresent)
        }

        @Test
        fun `should return 403 when user role tries to delete book`() {
            mockMvc.delete("/book/${testBook.id}") {
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isForbidden() }
            }

            // Verify book still exists
            Assertions.assertTrue(bookRepository.findById(testBook.id!!).isPresent)
        }

        @Test
        fun `should return 404 when deleting non-existent book`() {
            val nonExistentId = UUID.randomUUID()

            mockMvc.delete("/book/$nonExistentId") {
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
            }.andExpect {
                status { isNotFound() }
            }
        }
    }

    @Nested
    @DisplayName("GET /book/search - Search Books")
    inner class SearchBooksTests {

        @BeforeEach
        fun setupSearchData() {
            val author2 = authorRepository.save(TestDataBuilder.buildAuthor(name = "Stephen King"))
            val genre2 = genreRepository.save(TestDataBuilder.buildGenre(name = "Horror"))

            bookRepository.save(
                TestDataBuilder.buildBook(
                    title = "The Shining",
                    authors = listOf(author2),
                    genre = genre2
                )
            )
        }

        @Test
        fun `should search books by title`() {
            mockMvc.get("/book/search") {
                param("criteria", "Great")
                param("page", "0")
                param("size", "10")
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data") { isArray() }
                jsonPath("$.data[0].title") { value("The Great Book") }
            }
        }

        @Test
        fun `should search books by author name`() {
            mockMvc.get("/book/search") {
                param("criteria", "Stephen")
                param("page", "0")
                param("size", "10")
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data") { isArray() }
                jsonPath("$.data[0].title") { value("The Shining") }
            }
        }

        @Test
        fun `should return empty list when no matches found`() {
            mockMvc.get("/book/search") {
                param("criteria", "NonExistent")
                param("page", "0")
                param("size", "10")
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data") { isEmpty() }
                jsonPath("$.totalElements") { value(0) }
            }
        }

        @Test
        fun `should return 400 when search criteria is too short`() {
            mockMvc.get("/book/search") {
                param("criteria", "Te")
                param("page", "0")
                param("size", "10")
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }
}