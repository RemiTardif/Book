package com.example.demo

import com.example.demo.infrastructure.driven.postgres.BookDAO
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT(
    private val bookDAO: BookDAO,
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }
    }

    init {
        afterEach {
            namedParameterJdbcTemplate.update(
                "DELETE FROM book", MapSqlParameterSource()
            )
        }

        test("save sauvegarde un livre en base") {
            // Arrange
            val cesar = com.example.demo.domain.model.Book("Harry Potter", "Rowling")

            // Act
            bookDAO.save(cesar)

            // Assert
            val result = bookDAO.findAll()
            result shouldContain cesar
        }

        test("findAll retourne tous les livres") {
            // Arrange
            val book1 = com.example.demo.domain.model.Book("Harry Potter", "Rowling")
            val book2 = com.example.demo.domain.model.Book("Alice", "Auteur2")
            bookDAO.save(book1)
            bookDAO.save(book2)

            // Act
            val result = bookDAO.findAll()

            // Assert
            result.size shouldBe 2
            result shouldContain book1
            result shouldContain book2
        }
    }
}