Feature: gestion des livres

  Scenario: l'utilisateur crée un livre et récupère la liste
    Given l'utilisateur crée le livre avec le titre "Harry Potter" et l'auteur "Rowling"
    When l'utilisateur récupère tous les livres
    Then la liste contient le livre avec le titre "Harry Potter" et l'auteur "Rowling"

  Scenario: l'utilisateur crée un livre avec un titre vide et reçoit une erreur
    When l'utilisateur tente de créer le livre avec le titre "" et l'auteur "Rowling"
    Then la réponse a le status 400

  Scenario: l'utilisateur réserve un livre avec succès
    Given l'utilisateur crée le livre avec le titre "Harry Potter" et l'auteur "Rowling"
    When l'utilisateur réserve le livre créé
    Then la réponse a le status 200
    And le livre est marqué comme réservé

  Scenario: l'utilisateur tente de réserver un livre déjà réservé
    Given l'utilisateur crée le livre avec le titre "Harry Potter" et l'auteur "Rowling"
    Given l'utilisateur a déjà réservé le livre créé
    When l'utilisateur tente de réserver le livre créé à nouveau
    Then la réponse a le status 400