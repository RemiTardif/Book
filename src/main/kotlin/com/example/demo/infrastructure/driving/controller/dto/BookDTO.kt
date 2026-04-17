package com.example.demo.infrastructure.driving.controller.dto

data class BookDTO(
    val id: String? = null,
    val titre: String,
    val auteur: String,
    val reserved: Boolean = false
)