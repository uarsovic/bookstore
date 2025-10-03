package rs.ua.bookstore.dto

data class PagedDto<T>(
    val data: List<T>,
    val numberOfElements: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val hasNext: Boolean = false
)