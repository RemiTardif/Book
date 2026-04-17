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
            "INSERT INTO book (id, titre, auteur, reserved) VALUES (:id, :titre, :auteur, :reserved)",
            MapSqlParameterSource()
                .addValue("id", book.id)
                .addValue("titre", book.titre)
                .addValue("auteur", book.auteur)
                .addValue("reserved", book.reserved)
        )
    }

    override fun findAll(): List<Book> {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM book",
            MapSqlParameterSource()
        ) { rs, _ ->
            Book(
                id = rs.getString("id"),
                titre = rs.getString("titre"),
                auteur = rs.getString("auteur"),
                reserved = rs.getBoolean("reserved")
            )
        }
    }

    override fun findById(id: String): Book? {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM book WHERE id = :id",
            MapSqlParameterSource().addValue("id", id)
        ) { rs, _ ->
            Book(
                id = rs.getString("id"),
                titre = rs.getString("titre"),
                auteur = rs.getString("auteur"),
                reserved = rs.getBoolean("reserved")
            )
        }.firstOrNull()
    }

    override fun update(book: Book) {
        namedParameterJdbcTemplate.update(
            "UPDATE book SET reserved = :reserved WHERE id = :id",
            MapSqlParameterSource()
                .addValue("reserved", book.reserved)
                .addValue("id", book.id)
        )
    }
}