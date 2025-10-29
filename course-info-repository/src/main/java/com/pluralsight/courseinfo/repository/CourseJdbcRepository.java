package com.pluralsight.courseinfo.repository;

import com.pluralsight.courseinfo.domain.Course;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h2>Documentation de la classe {@code CourseJdbcRepository}</h2>
 *
 * <h3>Pour le Débutant : L'Implémentation du Contrat</h3>
 * <p>Cette classe est l'implémentation concrète de l'interface {@link CourseRepository}. C'est ici que le "comment"
 * est défini. Elle utilise la technologie <b>JDBC (Java Database Connectivity)</b> pour se connecter et interagir
 * avec une base de données H2.</p>
 *
 * <h3>Pour le Futur Expert : Choix d'Implémentation et Bonnes Pratiques</h3>
 * <p>Cette classe encapsule toute la logique de bas niveau spécifique à JDBC. L'utilisation de JDBC pur est un bon
 * choix pour des applications simples ou quand on veut un contrôle total sur les requêtes SQL. Pour des applications
 * plus complexes, un ORM (Object-Relational Mapper) comme Hibernate (via JPA) pourrait être utilisé pour réduire
 * encore plus le code "boilerplate" (répétitif).</p>
 */
public class CourseJdbcRepository implements CourseRepository {

    /**
     * <h3>Pour le Débutant : L'URL de Connexion</h3>
     * <p>C'est "l'adresse" de notre base de données. Elle indique au driver JDBC comment se connecter.</p>
     * <ul>
     *     <li>{@code jdbc:h2:file:%s}: Utilise le driver H2 et stocke la base de données dans un fichier local. {@code %s} sera remplacé par le nom du fichier.</li>
     *     <li>{@code AUTO_SERVER=TRUE}: Permet à plusieurs parties de l'application d'accéder à la base de données simultanément.</li>
     *     <li>{@code INIT=RUNSCRIPT FROM...}: Une commande très utile de H2 qui exécute le script SQL spécifié au démarrage. C'est ce qui crée notre table {@code COURSES} automatiquement.</li>
     * </ul>
     */
    private static final String H2_DATABASE_URL =
            "jdbc:h2:file:%s;AUTO_SERVER=TRUE;INIT=RUNSCRIPT FROM './db_init.sql'";

    /**
     * <h3>Pour le Débutant : Une Requête SQL Préparée</h3>
     * <p>C'est le modèle de notre requête SQL pour insérer ou mettre à jour un cours. Les points d'interrogation ({@code ?}) sont des
     * emplacements que nous remplirons plus tard avec les vraies données du cours.</p>
     *
     * <h3>Pour le Futur Expert : Sécurité et Performance avec les `PreparedStatement`</h3>
     * <p>Utiliser des {@link PreparedStatement} (des requêtes avec des {@code ?}) est <b>non négociable</b> en JDBC. Cela pré-compile la requête
     * côté base de données, ce qui améliore les performances si on l'exécute plusieurs fois. Plus important encore, cela empêche
     * les <b>injections SQL</b>, une des failles de sécurité les plus courantes, en s'assurant que les données fournies sont traitées
     * comme des valeurs et non comme du code SQL exécutable.</p>
     */
    private static final String INSERT_COURSE = """
            MERGE INTO COURSES (id, name, length, url)
            VALUES (?, ?, ?, ?)
            """;

    /**
     * <h3>Pour le Futur Expert : Le Pattern DataSource</h3>
     * <p>Une {@link DataSource} est une "usine" à connexions. C'est la manière standard et moderne d'obtenir des connexions
     * à une base de données. Elle abstrait les détails de la création de connexion et permet des fonctionnalités avancées
     * comme le "connection pooling" (réutilisation des connexions) pour des performances optimales dans une application multi-thread.</p>
     */
    private final DataSource dataSource;

    public CourseJdbcRepository(String databaseFile) {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl(H2_DATABASE_URL.formatted(databaseFile));
        this.dataSource = jdbcDataSource;
    }

    @Override
    public void saveCourse(Course course) {
        // <h3>Pour le Débutant : Obtenir une connexion et préparer une requête</h3>
        // <p>On demande une connexion à notre DataSource. Le `try-with-resources` garantit que la connexion sera fermée
        // automatiquement à la fin, même en cas d'erreur.</p>
        try (Connection connection = dataSource.getConnection()) {
            // <h3>Pour le Futur Expert : Gestion des Ressources JDBC</h3>
            // <p>Le code actuel a une faiblesse : le `PreparedStatement` n'est pas déclaré dans le bloc `try-with-resources`.
            // Si `connection.prepareStatement()` réussit mais que `statement.setString()` échoue, le `statement` ne sera
            // jamais fermé, créant une fuite de ressources. La version correcte et plus sûre serait :
            // `try (Connection conn = ...; PreparedStatement stmt = conn.prepareStatement(...)) { ... }`</p>
            PreparedStatement statement = connection.prepareStatement(INSERT_COURSE);
            statement.setString(1, course.id());
            statement.setString(2, course.name());
            statement.setLong(3, course.length());
            statement.setString(4, course.url());
            statement.execute();
        } catch (SQLException e) {
            // <h3>Pour le Débutant : Gérer les erreurs</h3>
            // <p>Si quelque chose se passe mal avec la base de données, une `SQLException` est lancée. On l'attrape
            // et on lance notre propre `RepositoryException` pour ne pas exposer les détails de JDBC.</p>
            throw new RepositoryException("failed to save " + course, e);
        }
    }

    @Override
    public List<Course> getAllCourses() {
        // <h3>Pour le Débutant : Lire des données</h3>
        // <p>Ici, on se connecte, on crée une requête simple (`Statement`) et on l'exécute. Le résultat est un `ResultSet`,
        // qui est un tableau de données que l'on peut parcourir ligne par ligne.</p>
        try (Connection connection = dataSource.getConnection()) {
            // <h3>Pour le Futur Expert : `Statement` vs `PreparedStatement`</h3>
            // <p>On utilise ici un `Statement` simple car la requête n'a pas de paramètres. C'est acceptable, mais il est
            // bon de savoir que même pour des requêtes sans paramètres, un `PreparedStatement` peut parfois être légèrement
            // plus performant s'il est réutilisé. De plus, la gestion des ressources ici a la même faiblesse que dans `saveCourse` :
            // le `Statement` et le `ResultSet` devraient être dans le bloc `try-with-resources` pour garantir leur fermeture.</p>
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM COURSES");

            List<Course> courses = new ArrayList<>();
            // <h3>Pour le Débutant : Parcourir les résultats</h3>
            // <p>La boucle `while (resultSet.next())` avance d'une ligne à chaque itération. Pour chaque ligne, on extrait
            // les données de chaque colonne pour créer un nouvel objet `Course`.</p>
            while (resultSet.next()) {
                // <h3>Pour le Futur Expert : La fragilité des index de colonnes</h3>
                // <p>Utiliser des numéros de colonne (`resultSet.getString(1)`) est rapide mais fragile. Si quelqu'un
                // change l'ordre des colonnes dans la requête `SELECT` (ou dans la table), le code se cassera de manière
                // silencieuse et difficile à déboguer (ex: le nom se retrouvera dans l'URL). Il est beaucoup plus robuste
                // d'utiliser le nom des colonnes : `resultSet.getString("ID")`, `resultSet.getString("NAME")`, etc.
                // De plus, le `SELECT *` est souvent déconseillé en production car il peut ramener des colonnes inutiles
                // et sa signification peut changer si la table est modifiée. Il est préférable de lister explicitement
                // les colonnes dont on a besoin (`SELECT id, name, length, url, notes FROM COURSES`).</p>
                Course course = new Course(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getLong(3),
                        resultSet.getString(4));
                courses.add(course);
            }
            // <h3>Pour le Futur Expert : Le Contrat de non-modification</h3>
            // <p>Retourner une liste non modifiable est une excellente pratique de programmation défensive. Cela empêche
            // le code qui appelle cette méthode de modifier la liste (ex: en faisant `list.add(...)`), ce qui pourrait
            // créer des incohérences avec l'état réel de la base de données.</p>
            return Collections.unmodifiableList(courses);
        } catch (SQLException e) {
            // <h3>Pour le Débutant : Un message d'erreur peu informatif</h3>
            // <p>Le message "failed to save" est incorrect ici (c'est un copier-coller de l'autre méthode) et devrait être
            // "failed to retrieve courses". C'est un petit bug qui montre l'importance de messages d'erreur clairs.</p>
            throw new RepositoryException("failed to save ", e);
        }
    }
}
