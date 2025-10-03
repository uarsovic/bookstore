package rs.ua.bookstore.entity

import jakarta.persistence.*
import org.hibernate.annotations.Generated
import org.hibernate.generator.EventType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "book")
@EntityListeners(AuditingEntityListener::class)
class Book(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Generated(event = [EventType.INSERT])
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    val id: UUID? = null,

    @CreatedDate
    @Column(name = "created_on")
    var createdOn: OffsetDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_on")
    var updatedOn: OffsetDateTime? = null,

    @Column(nullable = false, name = "title")
    val title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    val genre: Genre,

    @ManyToMany
    @JoinTable(
        name = "book_authors",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    val authors: List<Author>,

    @Column(nullable = false, name = "price")
    val price: BigDecimal
)