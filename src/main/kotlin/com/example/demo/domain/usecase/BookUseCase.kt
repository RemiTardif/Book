package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import java.util.UUID

class BookUseCase(private val bookRepository: BookRepository) {

    fun addBook(titre: String, auteur: String): Book {
        require(titre.isNotBlank()) { "Le titre ne peut pas être vide" }
        require(auteur.isNotBlank()) { "L'auteur ne peut pas être vide" }

        val book = Book(
            id = UUID.randomUUID().toString(),
            titre = titre,
            auteur = auteur
        )
        bookRepository.save(book)
        return book
    }

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.titre }
    }

    fun reserveBook(id: String): Book {
        val book = bookRepository.findById(id)
            ?: throw NoSuchElementException("Livre introuvable")

        require(!book.reserved) { "Le livre est déjà réservé" }

        val updatedBook = book.copy(reserved = true)
        bookRepository.update(updatedBook)
        return updatedBook
    }
}