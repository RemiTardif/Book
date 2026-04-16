package com.example.demo.infrastructure.driven.postgres

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : BookRepository {

    override fun save(book: Book) {
        namedParameterJdbcTemplate.update(
            "INSERT INTO book (titre, auteur) VALUES (:titre, :auteur)",
            MapSqlParameterSource()
                .addValue("titre", book.titre)
                .addValue("auteur", book.auteur)
        )
    }

    override fun findAll(): List<Book> {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM book",
            MapSqlParameterSource()
        ) { rs, _ ->
            Book(
                titre = rs.getString("titre"),
                auteur = rs.getString("auteur")
            )
        }
    }
}