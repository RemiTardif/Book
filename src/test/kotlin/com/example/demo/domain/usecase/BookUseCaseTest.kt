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
        result.titre shouldBe "Harry Potter"
        result.auteur shouldBe "Rowling"
        result.reserved shouldBe false
        verify { repository.save(result) }
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
            Book(id = "1", titre = "Zorro", auteur = "Auteur1"),
            Book(id = "2", titre = "Harry Potter", auteur = "Rowling"),
            Book(id = "3", titre = "Alice", auteur = "Auteur2")
        )

        // Act
        val result = useCase.getAllBooks()

        // Assert
        result shouldBe listOf(
            Book(id = "3", titre = "Alice", auteur = "Auteur2"),
            Book(id = "2", titre = "Harry Potter", auteur = "Rowling"),
            Book(id = "1", titre = "Zorro", auteur = "Auteur1")
        )
    }

    test("getAllBooks retourne toujours tous les livres stockés") {
        checkAll(Arb.string(1..20), Arb.string(1..20)) { titre, auteur ->
            // Arrange
            val repository = mockk<BookRepository>()
            val useCase = BookUseCase(repository)
            val book = Book(id = "1", titre = titre, auteur = auteur)
            every { repository.findAll() } returns listOf(book)

            // Act
            val result = useCase.getAllBooks()

            // Assert
            result shouldContain book
        }
    }

    test("reserveBook retourne le livre avec reserved = true") {
        // Arrange
        val repository = mockk<BookRepository>()
        val useCase = BookUseCase(repository)
        val book = Book(id = "123", titre = "Harry Potter", auteur = "Rowling", reserved = false)

        every { repository.findById("123") } returns book
        every { repository.update(any()) } returns Unit

        // Act
        val result = useCase.reserveBook("123")

        // Assert
        result.reserved shouldBe true
        result.titre shouldBe "Harry Potter"
        verify(exactly = 1) { repository.update(result) }
    }

    test("reserveBook sur un livre déjà réservé lance une exception") {
        // Arrange
        val repository = mockk<BookRepository>()
        val useCase = BookUseCase(repository)
        val book = Book(id = "123", titre = "Harry Potter", auteur = "Rowling", reserved = true)

        every { repository.findById("123") } returns book

        // Act & Assert
        shouldThrow<IllegalArgumentException> {
            useCase.reserveBook("123")
        }
    }

    test("reserveBook sur un livre introuvable lance une exception") {
        // Arrange
        val repository = mockk<BookRepository>()
        val useCase = BookUseCase(repository)

        every { repository.findById("999") } returns null

        // Act & Assert
        shouldThrow<NoSuchElementException> {
            useCase.reserveBook("999")
        }
    }
})