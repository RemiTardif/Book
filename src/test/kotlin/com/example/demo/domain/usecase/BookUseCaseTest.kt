package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.string
import io.kotest.matchers.collections.shouldContain
import io.kotest.property.Arb

class BookUseCaseTest : FunSpec({

    test("addBook crée un livre et le retourne") {
        // Arrange
        val repository = mockk<BookRepository>(relaxed = true)
        val useCase = BookUseCase(repository)

        // Act
        val result = useCase.addBook("Harry Potter", "Rowling")

        // Assert
        result shouldBe Book("Harry Potter", "Rowling")
        verify { repository.save(Book("Harry Potter", "Rowling")) }
    }

    test("addBook avec titre vide lance une exception") {
        // Arrange
        val repository = mockk<BookRepository>(relaxed = true)
        val useCase = BookUseCase(repository)
        // Act & Assert
        shouldThrow<IllegalArgumentException> {
            useCase.addBook("", "Rowling")
        }
    }

    test("addBook avec auteur vide lance une exception") {
        // Arrange
        val repository = mockk<BookRepository>(relaxed = true)
        val useCase = BookUseCase(repository)
        // Act & Assert
        shouldThrow<IllegalArgumentException> {
            useCase.addBook("Harry Potter", "")
        }
    }

    test("getAllBooks retourne les livres triés par ordre alphabétique") {
        // Arrange
        val repository = mockk<BookRepository>()
        val useCase = BookUseCase(repository)
        every { repository.findAll() } returns listOf(
            Book("Zorro", "Auteur1"),
            Book("Harry Potter", "Rowling"),
            Book("Alice", "Auteur2")
        )
        // Act
        val result = useCase.getAllBooks()
        // Assert
        result shouldBe listOf(
            Book("Alice", "Auteur2"),
            Book("Harry Potter", "Rowling"),
            Book("Zorro", "Auteur1")
        )
    }

    test("getAllBooks retourne toujours tous les livres stockés") {
        checkAll(Arb.string(1..20), Arb.string(1..20)) { titre, auteur ->
            // Arrange
            val repository = mockk<BookRepository>()
            val useCase = BookUseCase(repository)
            val book = Book(titre, auteur)
            every { repository.findAll() } returns listOf(book)
            // Act
            val result = useCase.getAllBooks()
            // Assert
            result shouldContain book
        }
    }

})
