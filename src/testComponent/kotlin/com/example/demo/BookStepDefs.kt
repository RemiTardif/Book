package com.example.demo

import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.response.Response
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class BookStepDefs(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) {

    @LocalServerPort
    private var port: Int? = 0

    private var lastResponse: Response? = null
    private var lastCreatedId: String? = null

    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        namedParameterJdbcTemplate.update("DELETE FROM book", MapSqlParameterSource())
    }

    @Given("l'utilisateur crée le livre avec le titre {string} et l'auteur {string}")
    fun createBook(titre: String, auteur: String) {
        lastResponse = RestAssured.given()
            .contentType("application/json")
            .body("""{"titre": "$titre", "auteur": "$auteur"}""")
            .`when`()
            .post("/books")
            .then()
            .extract()
            .response()
        lastCreatedId = lastResponse?.jsonPath()?.getString("id")
    }

    @When("l'utilisateur récupère tous les livres")
    fun getAllBooks() {
        lastResponse = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .extract()
            .response()
    }

    @When("l'utilisateur tente de créer le livre avec le titre {string} et l'auteur {string}")
    fun createBookWhen(titre: String, auteur: String) {
        lastResponse = RestAssured.given()
            .contentType("application/json")
            .body("""{"titre": "$titre", "auteur": "$auteur"}""")
            .`when`()
            .post("/books")
            .then()
            .extract()
            .response()
    }

    @When("l'utilisateur réserve le livre créé")
    fun reserveBook() {
        lastResponse = RestAssured.given()
            .contentType("application/json")
            .`when`()
            .post("/books/$lastCreatedId/reserve")
            .then()
            .extract()
            .response()
    }

    @Given("l'utilisateur a déjà réservé le livre créé")
    fun alreadyReserveBook() {
        RestAssured.given()
            .contentType("application/json")
            .`when`()
            .post("/books/$lastCreatedId/reserve")
            .then()
            .extract()
            .response()
    }

    @When("l'utilisateur tente de réserver le livre créé à nouveau")
    fun reserveBookAgain() {
        lastResponse = RestAssured.given()
            .contentType("application/json")
            .`when`()
            .post("/books/$lastCreatedId/reserve")
            .then()
            .extract()
            .response()
    }

    @Then("la liste contient le livre avec le titre {string} et l'auteur {string}")
    fun listContainsBook(titre: String, auteur: String) {
        lastResponse?.statusCode shouldBe 200
        lastResponse?.body?.asString()?.contains(titre) shouldBe true
        lastResponse?.body?.asString()?.contains(auteur) shouldBe true
    }

    @Then("la réponse a le status {int}")
    fun responseHasStatus(status: Int) {
        lastResponse?.statusCode shouldBe status
    }

    @Then("le livre est marqué comme réservé")
    fun bookIsReserved() {
        lastResponse?.statusCode shouldBe 200
        lastResponse?.jsonPath()?.getBoolean("reserved") shouldBe true
    }
}