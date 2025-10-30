package com.pluralsight.courseinfo.domain;

import java.util.Optional;

/**
 * Représente un cours stocké dans notre base de données.
 * C'est la représentation "domaine" d'un cours, ce qui signifie qu'elle est
 * indépendante de la source des données (Pluralsight, etc.).
 *
 * @param id L'identifiant unique du cours.
 * @param name Le nom du cours.
 * @param length La durée du cours en minutes.
 * @param url L'URL complète pour accéder au cours.
 * @param notes Un champ optionnel pour que les utilisateurs ajoutent des notes personnelles.
 */
public record Course(String id, String name, long length, String url, Optional<String> notes) {

    /**
     * Constructeur compact pour la validation.
     * Il est appelé automatiquement par le constructeur principal.
     * On s'assure ici que les valeurs fournies sont valides et non nulles.
     */
    public Course {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Course ID cannot be null or blank.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Course name cannot be null or blank.");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Course URL cannot be null or blank.");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Course length must be a positive value.");
        }
        if (notes != null && notes.isPresent() && notes.get().isBlank()) {
            throw new IllegalArgumentException("Course notes cannot be blank.");
        }

    }


}
