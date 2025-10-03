package rs.ua.bookstore.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import rs.ua.bookstore.dto.GenreDto
import rs.ua.bookstore.dto.PagedDto
import rs.ua.bookstore.repository.GenreRepository

interface GenreService {
    fun getAllGenres(pageable: Pageable): PagedDto<GenreDto>
}

@Service
class GenreServiceImpl(
    private val genreRepository: GenreRepository
) : GenreService {

    override fun getAllGenres(pageable: Pageable): PagedDto<GenreDto> {
        val pagedGenres = genreRepository.findAll(pageable).map { genre -> GenreDto(genre.id!!, genre.name) }
        return PagedDto(
            pagedGenres.toList(),
            pagedGenres.numberOfElements,
            pagedGenres.totalElements,
            pagedGenres.totalPages,
            pagedGenres.hasNext()
        )
    }
}