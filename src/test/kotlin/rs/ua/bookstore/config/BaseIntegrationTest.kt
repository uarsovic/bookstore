package rs.ua.bookstore.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [BaseIntegrationTest.Initializer::class])
@Testcontainers
@ActiveProfiles("test")
class BaseIntegrationTest {
    companion object {
        private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withInitScript("db/init_schema.sql")
            withDatabaseName("bookstore_test")
            withUsername("test")
            withPassword("test")
            withReuse(true)
            start()
        }
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=${postgresContainer.jdbcUrl}",
                "spring.datasource.username=${postgresContainer.username}",
                "spring.datasource.password=${postgresContainer.password}",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.flyway.enabled=false"
            ).applyTo(applicationContext.environment)
        }
    }
}