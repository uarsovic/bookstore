package rs.ua.bookstore.controller

import jakarta.annotation.security.RolesAllowed
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rs.ua.bookstore.config.Roles
import rs.ua.bookstore.service.GenreService

@RestController
@RequestMapping("/genres")
@Validated
class GenreController(private val genreService: GenreService) {

    @GetMapping
    @RolesAllowed(Roles.USER, Roles.ADMIN)
    fun getGenres(pageable: Pageable) = genreService.getAllGenres(pageable)
}