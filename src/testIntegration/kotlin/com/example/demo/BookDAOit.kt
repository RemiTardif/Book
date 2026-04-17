package com.example.demo

import com.example.demo.domain.model.Book
import com.example.demo.infrastructure.driven.postgres.BookDAO
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
            val book = Book(id = "1", titre = "Harry Potter", auteur = "Rowling")

            // Act
            bookDAO.save(book)

            // Assert
            val result = bookDAO.findAll()
            result shouldContain book
        }

        test("findAll retourne tous les livres") {
            // Arrange
            val book1 = Book(id = "1", titre = "Harry Potter", auteur = "Rowling")
            val book2 = Book(id = "2", titre = "Alice", auteur = "Auteur2")
            bookDAO.save(book1)
            bookDAO.save(book2)

            // Act
            val result = bookDAO.findAll()

            // Assert
            result.size shouldBe 2
            result shouldContain book1
            result shouldContain book2
        }

        test("findById retourne le livre correspondant") {
            // Arrange
            val book = Book(id = "1", titre = "Harry Potter", auteur = "Rowling")
            bookDAO.save(book)

            // Act
            val result = bookDAO.findById("1")

            // Assert
            result shouldNotBe null
            result!!.id shouldBe "1"
            result.titre shouldBe "Harry Potter"
            result.reserved shouldBe false
        }

        test("findById retourne null si le livre n'existe pas") {
            // Act
            val result = bookDAO.findById("999")

            // Assert
            result shouldBe null
        }

        test("update modifie le reserved du livre en base") {
            // Arrange
            val book = Book(id = "1", titre = "Harry Potter", auteur = "Rowling", reserved = false)
            bookDAO.save(book)

            // Act
            bookDAO.update(book.copy(reserved = true))

            // Assert
            val result = bookDAO.findById("1")
            result!!.reserved shouldBe true
        }
    }
}