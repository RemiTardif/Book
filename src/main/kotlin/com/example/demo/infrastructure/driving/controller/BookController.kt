package com.example.demo.infrastructure.driving.controller

import com.example.demo.domain.usecase.BookUseCase
import com.example.demo.infrastructure.driving.controller.dto.BookDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val bookUseCase: BookUseCase) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks().map { book ->
            BookDTO(
                id = book.id,
                titre = book.titre,
                auteur = book.auteur,
                reserved = book.reserved
            )
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDTO: BookDTO): BookDTO {
        val book = bookUseCase.addBook(bookDTO.titre, bookDTO.auteur)
        return BookDTO(
            id = book.id,
            titre = book.titre,
            auteur = book.auteur,
            reserved = book.reserved
        )
    }

    @PostMapping("/{id}/reserve")
    fun reserveBook(@PathVariable id: String): BookDTO {
        val book = bookUseCase.reserveBook(id)
        return BookDTO(
            id = book.id,
            titre = book.titre,
            auteur = book.auteur,
            reserved = book.reserved
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(e: IllegalArgumentException) {}

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(e: NoSuchElementException) {}

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalError(e: RuntimeException) {}
}