package rs.ua.bookstore.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import rs.ua.bookstore.dto.AuthorDto
import rs.ua.bookstore.dto.PagedDto
import rs.ua.bookstore.repository.AuthorRepository

interface AuthorService {
    fun getAllAuthors(pageable: Pageable): PagedDto<AuthorDto>
}

@Service
class AuthorServiceImpl(
    private val authorRepository: AuthorRepository
) : AuthorService {
    override fun getAllAuthors(pageable: Pageable): PagedDto<AuthorDto> {
        val pagedAuthors = authorRepository.findAll(pageable).map { AuthorDto(it.id!!, it.name) }
        return PagedDto(
            pagedAuthors.toList(),
            pagedAuthors.numberOfElements,
            pagedAuthors.totalElements,
            pagedAuthors.totalPages,
            pagedAuthors.hasNext()
        )
    }
}