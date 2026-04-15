package com.example.demo

import com.example.demo.domain.model.Book
import com.example.demo.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest
class BookControllerIT(
    private val mockMvc: MockMvc,
    @MockkBean val bookUseCase: BookUseCase
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        test("GET /books retourne la liste des livres") {
            // Arrange
            every { bookUseCase.getAllBooks() } returns listOf(
                Book("Harry Potter", "Rowling"),
                Book("Alice", "Auteur2")
            )

            // Act & Assert
            mockMvc.get("/books") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    json("""
                        [
                            {"titre": "Harry Potter", "auteur": "Rowling"},
                            {"titre": "Alice", "auteur": "Auteur2"}
                        ]
                    """.trimIndent())
                }
            }
        }


        test("POST /books crée un livre et retourne 201") {
            // Arrange
            every { bookUseCase.addBook("Harry Potter", "Rowling") } returns Book("Harry Potter", "Rowling")

            // Act & Assert
            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"titre": "Harry Potter", "auteur": "Rowling"}"""
            }.andExpect {
                status { isCreated() }
                content {
                    json("""{"titre": "Harry Potter", "auteur": "Rowling"}""")
                }
            }
        }

        test("POST /books avec titre vide retourne 400") {
            // Arrange
            every { bookUseCase.addBook("", "Rowling") } throws IllegalArgumentException("Le titre ne peut pas être vide")

            // Act & Assert
            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"titre": "", "auteur": "Rowling"}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }

        test("POST /books avec une erreur inattendue retourne 500") {
            // Arrange
            every { bookUseCase.addBook("Harry Potter", "Rowling") } throws RuntimeException("Erreur inattendue")

            // Act & Assert
            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"titre": "Harry Potter", "auteur": "Rowling"}"""
            }.andExpect {
                status { isInternalServerError() }
            }
        }
    }
}