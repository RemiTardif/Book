package com.example.demo

class Cesar {
    fun cypher(char: Char, key: Int): Char {

        require(key >= 0) { "La clé doit être >= 0" }

        val shifted = (char.code - 'A'.code + key) % 26
        return ('A'.code + shifted).toChar()
    }
}