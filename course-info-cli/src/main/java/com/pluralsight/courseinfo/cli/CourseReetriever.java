package com.pluralsight.courseinfo.cli;

import com.pluralsight.courseinfo.cli.service.CourseReetrievalService;
import com.pluralsight.courseinfo.cli.service.CourseStorageService;
import com.pluralsight.courseinfo.cli.service.PluralsightCourse;
import com.pluralsight.courseinfo.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

/**
 * <h2>Documentation de la classe {@code CourseReetriever}</h2>
 *
 * <h3>Pour le Débutant : Le Point d'Entrée de l'Application</h3>
 * <p>Cette classe est le point de départ de notre application en ligne de commande (CLI - Command-Line Interface).
 * La méthode {@code public static void main(String... args)} est une méthode spéciale en Java. C'est la toute
 * première chose que Java exécute quand on lance ce programme. Elle reçoit les arguments passés en ligne de commande
 * dans le tableau de chaînes de caractères {@code args}.</p>
 *
 * <h3>Pour le Futur Expert : Rôle de "Chef d'Orchestre" et Couches de Service</h3>
 * <p>Cette classe joue le rôle de <b>chef d'orchestre</b> ou de <b>contrôleur</b>. Sa responsabilité n'est pas de
 * faire le travail elle-même, mais de coordonner les différentes briques de l'application (les "services")
 * pour accomplir une tâche. Observez qu'elle ne contient pas de logique complexe de récupération de données
 * ou de stockage. Elle délègue ces tâches à {@link CourseReetrievalService} et {@link CourseStorageService}.
 * C'est une excellente pratique de conception appelée <b>Separation of Concerns</b> (Séparation des préoccupations),
 * qui rend le code plus modulaire, plus facile à tester et à maintenir.</p>
 */
public class CourseReetriever {
    /**
     * <h3>Pour le Débutant : Un Journal pour l'Application</h3>
     * <p>Un "Logger" est un outil qui permet d'afficher des messages sur la console (ou dans des fichiers) de manière
     * structurée. C'est beaucoup mieux qu'un simple {@code System.out.println()} car on peut configurer des niveaux
     * de log (INFO, WARN, ERROR) et contrôler finement ce qui est affiché.</p>
     *
     * <h3>Pour le Futur Expert : L'API SLF4J</h3>
     * <p>Nous utilisons SLF4J (Simple Logging Facade for Java). C'est une <b>façade</b>, c'est-à-dire une abstraction
     * qui nous permet d'écrire notre code de logging sans nous coupler à une implémentation spécifique (comme Logback,
     * Log4j2, etc.). On peut changer l'implémentation de logging en production sans changer une seule ligne de ce code,
     * simplement en modifiant les dépendances Maven. C'est le même principe de découplage que pour le Repository.</p>
     */
    private static final Logger LOG = LoggerFactory.getLogger(CourseReetriever.class);

    public static void main(String... args){
       LOG.info("Bienvenue dans le cours Pluralsight!");

        // On vérifie si l'utilisateur a fourni un argument en ligne de commande.
        if(args.length == 0){
            LOG.warn("Veuillez fournir le nom de l'auteur en argument.");
            return; // On arrête l'exécution si l'argument est manquant.
        }

        try {
            // On appelle la méthode principale qui contient la logique de l'application.
            retrieveCourses(args[0]);
        } catch (Exception e) {
            // On attrape toute erreur inattendue qui pourrait survenir pour l'afficher proprement.
            LOG.error("Une erreur inattendue est survenue: ", e);
            // Pour le futur expert : En production, il est crucial de logger l'exception `e` elle-même,
            // et pas seulement `e.getMessage()`, pour avoir la `stack trace` complète qui est essentielle au débogage.
        }
    }

    /**
     * <h3>Pour le Débutant : La Logique Principale</h3>
     * <p>Cette méthode orchestre les étapes : 1. Récupérer les cours depuis Pluralsight, 2. Filtrer les cours non désirés,
     * 3. Afficher les cours trouvés, 4. Les sauvegarder dans la base de données.</p>
     *
     * <h3>Pour le Futur Expert : Instanciation et API Stream</h3>
     * <p><b>Instanciation :</b> Notez la création directe des services (`new CourseReetrievalService()`, etc.). Dans une petite
     * application comme celle-ci, c'est acceptable. Dans une application plus grande, on utiliserait un framework
     * d'<b>Injection de Dépendances</b> (comme Spring, Guice, ou CDI) pour créer et "injecter" ces services automatiquement.
     * Cela améliore encore le découplage et la testabilité.</p>
     *
     * <p><b>API Stream :</b> L'utilisation de {@code .stream().filter(...).toList()} est un excellent exemple de programmation
     * fonctionnelle en Java. C'est une manière déclarative et très lisible de traiter des collections de données.
     * {@code Predicate.not(PluralsightCourse::isRetired)} est une syntaxe élégante (une référence de méthode) pour
     * filtrer et ne garder que les cours qui ne sont pas retirés.</p>
     *
     * @param authorId L'identifiant de l'auteur fourni en ligne de commande.
     */
    private static void retrieveCourses(String authorId) {
        LOG.info("Récupération des cours pour l'auteur '{}'", authorId);

        // 1. Création des services et du repository.
        CourseReetrievalService courseReetrievalService = new CourseReetrievalService();
        CourseRepository courseRepository = CourseRepository.openCourseRepository("./courses.db");
        CourseStorageService courseStorageService = new CourseStorageService(courseRepository);

        // 2. Appel au service de récupération pour obtenir les cours depuis l'API externe.
        List<PluralsightCourse> coursesToStore = courseReetrievalService.getCoursesFor(authorId)
                .stream() // On transforme la liste de cours en un "flux" (stream) de données.
                // 3. On filtre le flux pour ne garder que les cours qui ne sont pas retirés.
                .filter(Predicate.not(PluralsightCourse::isRetired))
                // 4. On retransforme le flux filtré en une nouvelle liste.
                .toList();

        LOG.info("{} cours trouvés pour l'auteur.", coursesToStore.size());

        // 5. Appel au service de stockage pour sauvegarder ces cours dans la base de données.
        courseStorageService.storePluralsightCourses(coursesToStore);

        LOG.info("Cours sauvegardés avec succès.");
    }
}
