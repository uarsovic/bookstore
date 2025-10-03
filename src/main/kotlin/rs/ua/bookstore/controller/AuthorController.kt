package rs.ua.bookstore.controller

import jakarta.annotation.security.RolesAllowed
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rs.ua.bookstore.config.Roles
import rs.ua.bookstore.service.AuthorService

@RestController
@RequestMapping("/author")
@Validated
class AuthorController(private val authorService: AuthorService) {

    @GetMapping
    @RolesAllowed(Roles.USER, Roles.ADMIN)
    fun getAuthors(pageable: Pageable) = authorService.getAllAuthors(pageable)
}