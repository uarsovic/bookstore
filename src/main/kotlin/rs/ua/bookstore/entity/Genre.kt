package rs.ua.bookstore.entity

import jakarta.persistence.*
import org.hibernate.annotations.Generated
import org.hibernate.generator.EventType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "genre")
class Genre(
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

    @Column(nullable = false, name = "name")
    val name: String
)