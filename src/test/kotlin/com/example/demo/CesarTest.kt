package com.example.demo
import io.kotest.assertions.throwables.shouldThrow

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CesarTest : FunSpec( {

    test("cypher A avec clé 2 retourne C") {
        // Arrange
        val cesar = Cesar()

        // Act
        val res = cesar.cypher('A', 2)

        // Assert
        res shouldBe 'C'
    }

    test("cypher Z avec clé 1 retourne A") {
        // Arrange
        val cesar = Cesar()

        // Act
        val res = cesar.cypher('Z', 1)

        // Assert
        res shouldBe 'A'
    }

    test("cypher A avec clé 27 retourne B") {
        // Arrange
        val cesar = Cesar()

        // Act
        val res = cesar.cypher('A', 27)

        // Assert
        res shouldBe 'B'
    }

    test("cypher avec clé négative lance une exception") {
        // Arrange
        val cesar = Cesar()

        // Act & Assert
        shouldThrow<IllegalArgumentException> {
            cesar.cypher('A', -1)
        }
    }

})