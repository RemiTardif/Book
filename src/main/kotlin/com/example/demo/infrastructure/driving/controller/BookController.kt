package com.example.demo.infrastructure.driving.controller

import com.example.demo.domain.usecase.BookUseCase
import com.example.demo.infrastructure.driving.controller.dto.BookDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val bookUseCase: BookUseCase) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks().map { book ->
            BookDTO(book.titre, book.auteur)
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDTO: BookDTO): BookDTO {
        val book = bookUseCase.addBook(bookDTO.titre, bookDTO.auteur)
        return BookDTO(book.titre, book.auteur)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(e: IllegalArgumentException) {}

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalError(e: RuntimeException) {}

}