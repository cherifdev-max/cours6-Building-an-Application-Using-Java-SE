/**
 * Requête SQL pour créer la table 'COURSES' si elle n'existe pas déjà.
 * Cette table est utilisée pour stocker les informations sur les cours.
 */
CREATE TABLE IF NOT EXISTS COURSES (
    -- Identifiant unique pour le cours. Sert de clé primaire.
    ID VARCHAR PRIMARY KEY NOT NULL,

    -- Le nom du cours. Ne peut pas être nul.
    NAME VARCHAR NOT NULL,

    -- La durée du cours, stockée en tant qu'entier (long). Ne peut pas être nul.
    LENGTH INT NOT NULL,

    -- L'URL (lien web) associée au cours. Ne peut pas être nulle.
    URL VARCHAR NOT NULL,

    -- Notes ou description additionnelles pour le cours. Peut être nul.
    NOTES VARCHAR
);