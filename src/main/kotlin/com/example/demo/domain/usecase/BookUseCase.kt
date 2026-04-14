package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository


class BookUseCase(private val bookRepository: BookRepository) {

    fun addBook(titre: String, auteur: String): Book {
        require(titre.isNotBlank()) { "Le titre ne peut pas être vide" }
        require(auteur.isNotBlank()) { "L'auteur ne peut pas être vide" }

        val book = Book(titre, auteur)
        bookRepository.save(book)
        return book
    }

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.titre }
    }
}