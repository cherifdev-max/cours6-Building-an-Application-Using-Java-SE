
package com.pluralsight.courseinfo.cli.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Cette classe est un "service" responsable de la récupération des informations sur les cours
 * depuis une source externe (ici, l'API de Pluralsight).
 * Un service, dans une application, a souvent pour rôle de gérer une logique métier spécifique.
 * Ici, son métier est de communiquer avec une API web.
 */
public class CourseReetrievalService {

    // Une constante (final) privée (private) et statique (static).
    // private : accessible uniquement depuis l'intérieur de cette classe.
    // static : cette variable appartient à la classe elle-même, et non à une instance de la classe.
    //          Il n'y aura qu'une seule copie de cette variable pour toute l'application.
    // final : sa valeur ne peut pas être changée après sa première assignation.
    // PS_URI contient l'URL de l'API de Pluralsight. Le "%s" est un placeholder (un emplacement)
    // qui sera remplacé par un véritable ID d'auteur plus tard.
    private static final String PS_URI = "https://app.pluralsight.com/profile/data/author/%s/all-content";

    // On crée un client HTTP. C'est l'objet qui va nous permettre d'envoyer des requêtes sur Internet.
    // Il est également 'static' et 'final' car on veut le créer une seule fois et le réutiliser
    // pour toutes les requêtes, ce qui est beaucoup plus efficace.
    private static final HttpClient CLIENT = HttpClient
            .newBuilder() // On utilise un "builder" pour construire notre client avec une configuration spécifique.
            .followRedirects(HttpClient.Redirect.ALWAYS) // On configure le client pour qu'il suive automatiquement les redirections HTTP.
            .build(); // On finalise la création du client.
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    /**
     * Récupère les informations des cours pour un auteur donné.
     *
     * @param authorId L'identifiant unique de l'auteur pour lequel on veut les cours.
     * @return Une chaîne de caractères (String) contenant les données des cours, généralement au format JSON.
     * Retourne une chaîne vide si l'auteur n'est pas trouvé (erreur 404).
     * @throws RuntimeException Si la requête HTTP échoue pour une autre raison (problème de réseau, serveur indisponible, etc.).
     */
    public List<PluralsightCourse> getCoursesFor(String authorId) {
        // On crée une requête HTTP.
        // URI.create(PS_URI.formatted(authorId)) : On remplace le "%s" dans notre URL par le vrai authorId.
        HttpRequest request = HttpRequest.newBuilder(URI.create(PS_URI.formatted(authorId)))
                .GET() // On spécifie que c'est une requête de type GET (pour récupérer des données).
                .build(); // On finalise la création de la requête.

        try {
            // On envoie la requête et on attend la réponse.
            // CLIENT.send(...) : envoie la requête préparée.
            // HttpResponse.BodyHandlers.ofString() : on indique qu'on s'attend à recevoir la réponse sous forme de texte (String).
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // On analyse le code de statut de la réponse pour savoir si tout s'est bien passé.
            // Un "switch expression" (disponible dans les versions récentes de Java) est utilisé ici.
            // C'est une manière concise d'écrire des conditions multiples.
            return switch (response.statusCode()) {
                case 200 -> toPtluralsightCourses(response);
                case 404 -> List.of(); // 404 Not Found : L'auteur n'a pas été trouvé. On retourne une chaîne vide.
                default ->
                        throw new RuntimeException("L'accès à Pluralsight a échoué avec le code : " + response.statusCode());
            };
        } catch (IOException | InterruptedException e) {
            // On capture les erreurs potentielles lors de l'envoi de la requête.
            // IOException : erreur de réseau (ex: pas de connexion internet).
            // InterruptedException : si le thread en cours est interrompu pendant l'attente.
            // On "enveloppe" l'exception originale dans une RuntimeException.
            // C'est une façon de gérer les erreurs : on arrête le programme en cas de problème grave
            // tout en conservant l'information sur la cause initiale de l'erreur.
            throw new RuntimeException("L'accès à Pluralsight a échoué", e);
        }
    }

    private static List<PluralsightCourse> toPtluralsightCourses(HttpResponse<String> response) throws JsonProcessingException {
        JavaType returnType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, PluralsightCourse.class);
        return OBJECT_MAPPER.readValue(response.body(), returnType);
    }
}
