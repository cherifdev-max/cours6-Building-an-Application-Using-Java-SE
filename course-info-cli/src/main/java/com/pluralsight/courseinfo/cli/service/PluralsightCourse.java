package com.pluralsight.courseinfo.cli.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Duration;
import java.time.LocalTime;

/**
 * <h2>Documentation du Record {@code PluralsightCourse}</h2>
 *
 * <p>Un {@code record} est une structure de données spéciale en Java (depuis Java 16)
 * conçue pour être un "transporteur de données immuable". Cela signifie qu'une fois
 * qu'un objet {@code PluralsightCourse} est créé, ses données ne peuvent plus être modifiées.</p>
 *
 * <p><b>Rôle dans l'application :</b></p>
 * <p>Ce record a pour unique but de modéliser les informations d'un cours tel qu'il est
 * reçu depuis l'API de Pluralsight. Il sert de DTO (Data Transfer Object), c'est-à-dire
 * un objet simple qui transporte les données brutes depuis la couche de service (qui appelle l'API)
 * vers d'autres parties de l'application qui en auraient besoin.</p>
 *
 * <p><b>Avantages de l'utilisation d'un Record ici :</b></p>
 * <ul>
 *     <li><b>Immutabilité :</b> Les données sont garanties de ne pas être modifiées accidentellement
 *     après leur création. C'est un gage de sécurité et de prévisibilité.</li>
 *     <li><b>Concision :</b> Le compilateur Java génère automatiquement pour nous le constructeur,
 *     les méthodes d'accès (comme {@code .title()}), ainsi que les méthodes {@code equals()},
 *     {@code hashCode()} et {@code toString()}, ce qui réduit drastiquement le code à écrire.</li>
 *     <li><b>Clarté :</b> En voyant le mot-clé {@code record}, un développeur sait immédiatement
 *     que le but de cette classe est de stocker des données.</li>
 * </ul>
 *
 * <p><b>Interaction avec Jackson :</b></p>
 * <p>L'annotation {@code @JsonIgnoreProperties(ignoreUnknown = true)} est cruciale. Elle indique à la
 * bibliothèque Jackson (qui convertit le JSON en objet Java) d'ignorer toutes les propriétés
 * du JSON qui ne sont pas définies dans ce record. C'est très utile car l'API de Pluralsight
 * pourrait renvoyer beaucoup d'autres champs qui ne nous intéressent pas. Sans cette annotation,
 * Jackson lèverait une erreur.</p>
 *
 * @param id L'identifiant unique du cours (ex: "java-fundamentals").
 * @param title Le titre complet du cours (ex: "Java Fundamentals: The Java Language").
 * @param duration La durée du cours sous forme de chaîne de caractères (ex: "08:34:22").
 *                 Ce format sera ensuite parsé par des méthodes spécifiques.
 * @param contentUrl L'URL relative pour accéder au contenu du cours sur Pluralsight.
 * @param isRetired Un booléen qui indique si le cours est "retiré" (true) ou toujours actif (false).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PluralsightCourse(String id, String title, String duration, String contentUrl, boolean isRetired) {

    /**
     * Méthode utilitaire pour convertir la durée textuelle du cours en un nombre total de minutes.
     *
     * <p>La durée est initialement une chaîne de caractères au format "HH:mm:ss".
     * Cette méthode la transforme en un format plus facilement manipulable (un nombre entier de minutes).</p>
     *
     * <p><b>Exemple de fonctionnement :</b></p>
     * <pre>
     *     String durationStr = "01:30:00";
     *     // LocalTime.parse(durationStr) -> crée un objet LocalTime pour 1h30 du matin.
     *     // Duration.between(LocalTime.MIN, ...) -> calcule la durée entre minuit (00:00) et 1h30.
     *     // .toMinutes() -> convertit cette durée en minutes, soit 90.
     * </pre>
     *
     * @return Le nombre total de minutes du cours, sous forme de {@code long}.
     */
    public long durationInMunite(){
        // LocalTime.MIN représente le début de la journée (00:00:00).
        // LocalTime.parse(duration) convertit la chaîne de caractères (ex: "08:34:22") en un objet de temps.
        // Duration.between(...) calcule la durée écoulée entre ces deux moments.
        // .toMinutes() retourne le résultat final en minutes.
        return Duration.between(LocalTime.MIN, LocalTime.parse(duration)).toMinutes();
    }

}
