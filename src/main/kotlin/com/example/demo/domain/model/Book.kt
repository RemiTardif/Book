package com.example.demo.domain.model

data class Book(
    val id: String,
    val titre: String,
    val auteur: String,
    val reserved: Boolean = false
)