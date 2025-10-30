package com.pluralsight.courseinfo.cli.service;

import com.pluralsight.courseinfo.domain.Course;
import com.pluralsight.courseinfo.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

/**
 * <h2>Documentation de la classe {@code CourseStorageService}</h2>
 *
 * <h3>Pour le Débutant : Un Service pour Sauvegarder</h3>
 * <p>Le rôle de cette classe est de prendre les cours récupérés depuis Pluralsight et de les sauvegarder
 * dans notre base de données. Elle agit comme un pont entre les données brutes de l'API (les objets
 * {@link PluralsightCourse}) et le format de données de notre application (les objets {@link Course}).</p>
 *
 * <h3>Pour le Futur Expert : Le Pattern Service et l'Injection de Dépendances</h3>
 * <p>Cette classe est un autre exemple de <b>Service</b>. Sa seule responsabilité est de gérer la logique de stockage.
 * Notez son constructeur : {@code public CourseStorageService(CourseRepository courseRepository)}. Il ne crée pas
 * lui-même le {@code CourseRepository}, il le reçoit en paramètre. C'est une pratique fondamentale appelée
 * <b>Injection de Dépendances (Dependency Injection)</b>.</p>
 * <p><b>Avantages de l'Injection de Dépendances :</b></p>
 * <ul>
 *     <li><b>Découplage :</b> Ce service ne dépend que de l'<b>abstraction</b> {@code CourseRepository}, pas d'une
 *     implémentation concrète. Il ne sait pas si les données sont stockées via JDBC, JPA ou dans un fichier.</li>
 *     <li><b>Testabilité :</b> En test unitaire, on peut facilement "injecter" un faux repository (un mock) pour
 *     vérifier que la méthode {@code storePluralsightCourses} appelle bien la méthode {@code saveCourse} du repository
 *     avec les bonnes données, sans avoir besoin d'une vraie base de données.</li>
 *     <li><b>Flexibilité :</b> On peut changer l'implémentation du repository sans jamais toucher à ce service.</li>
 * </ul>
 */
public class CourseStorageService {
    private static final String PS_BASE_URL = "https://app.pluralsight.com";

    private final CourseRepository courseRepository;

    public CourseStorageService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * <h3>Pour le Débutant : Sauvegarder une Liste de Cours</h3>
     * <p>Cette méthode parcourt la liste de cours reçus de Pluralsight. Pour chaque cours, elle le transforme
     * en un objet {@link Course} (le format de notre base de données) et demande au repository de le sauvegarder.</p>
     *
     * <h3>Pour le Futur Expert : Logique de Mapping et Transactionnalité</h3>
     * <p><b>Mapping :</b> La ligne {@code new Course(...)} effectue un <b>mapping</b> (une transformation) entre le DTO
     * (Data Transfer Object) {@code PluralsightCourse} et l'entité de domaine {@code Course}. C'est une responsabilité
     * très courante pour une couche de service. On adapte les données d'une source externe au modèle de notre propre application
     * (par exemple, en convertissant la durée en minutes et en construisant une URL complète).</p>
     *
     * <p><b>Transactionnalité :</b> Dans une application réelle, cette méthode devrait être <b>transactionnelle</b>. Cela
     * signifie que si la sauvegarde du 5ème cours sur 10 échoue, on devrait annuler (rollback) la sauvegarde des 4 premiers.
     * Toutes les sauvegardes devraient réussir, ou aucune. Les frameworks comme Spring fournissent des annotations
     * simples ({@code @Transactional}) pour gérer cela. En JDBC pur, il faudrait gérer la transaction manuellement
     * avec {@code connection.setAutoCommit(false)}, {@code connection.commit()} et {@code connection.rollback()}.
     * Pour cette application simple, l'absence de transaction est acceptable.</p>
     *
     * @param psCourses La liste des cours provenant de Pluralsight à sauvegarder.
     */
    public void storePluralsightCourses(List<PluralsightCourse> psCourses) {
        for (PluralsightCourse psCourse : psCourses) {
            // Pour chaque cours de Pluralsight, on crée un nouvel objet Course de notre domaine.
            Course course = new Course(psCourse.id(),
                                     psCourse.title(),
                                     psCourse.durationInMunite(),
                                     PS_BASE_URL + psCourse.contentUrl(), Optional.empty()
                                    ); // On initialise les notes à vide.
            // On demande au repository de sauvegarder ce nouvel objet Course.
            courseRepository.saveCourse(course);
        }
    }
}
