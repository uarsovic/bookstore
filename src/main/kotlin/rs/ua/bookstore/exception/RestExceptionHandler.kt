package rs.ua.bookstore.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: NotFoundException): ResponseEntity<ApiError> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError(HttpStatus.NOT_FOUND.value(), exception.message!!))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(exception: ConstraintViolationException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    exception.constraintViolations.joinToString(separator = "; ") { it.message }
                )
            )
}

data class ApiError(val status: Int, val message: String)