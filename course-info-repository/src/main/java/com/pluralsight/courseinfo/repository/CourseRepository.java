package com.pluralsight.courseinfo.repository;

import com.pluralsight.courseinfo.domain.Course;

import java.util.List;

/**
 * <h2>Documentation de l'interface {@code CourseRepository}</h2>
 *
 * <h3>Pour le Débutant : Qu'est-ce qu'une interface ?</h3>
 * <p>Pensez à une interface comme à un <b>contrat</b> ou un <b>plan directeur</b>. Elle définit un ensemble de
 * capacités (des méthodes) qu'une classe doit obligatoirement posséder si elle "implémente" cette interface.
 * L'interface dit <b>CE QU'IL FAUT FAIRE</b> (ex: "il faut pouvoir sauvegarder un cours"), mais pas
 * <b>COMMENT LE FAIRE</b>. Le "comment" est le travail de la classe qui implémente l'interface.</p>
 *
 * <p>Ici, {@code CourseRepository} est le contrat pour toute classe qui veut gérer la persistance (le stockage et la récupération)
 * des objets {@link Course}.</p>
 *
 * <h3>Pour le Futur Expert : Le "Pourquoi" de cette interface (Le Design Pattern Repository)</h3>
 * <p>Cette interface est au cœur d'un des principes de design les plus importants en ingénierie logicielle :
 * le <b>Dependency Inversion Principle</b> (le 'D' de SOLID). L'idée est de créer une <b>abstraction</b>
 * (cette interface) qui sépare la logique métier de haut niveau (ex: "notre application gère des cours")
 * des détails d'implémentation de bas niveau (ex: "on stocke les cours dans une base de données H2 avec JDBC").</p>
 *
 * <p><b>Les avantages sont immenses :</b></p>
 * <ul>
 *     <li><b>Découplage :</b> Les autres parties de l'application (les "clients" de ce repository) ne dépendent
 *     que de l'interface {@code CourseRepository}, pas de la classe {@code CourseJdbcRepository}. Elles ne savent
 *     pas et n'ont pas à savoir comment la persistance est gérée.</li>
 *     <li><b>Flexibilité et Interchangeabilité :</b> Aujourd'hui, nous utilisons JDBC. Demain, nous pourrions décider
 *     d'utiliser un autre framework comme JPA/Hibernate. Il nous suffirait de créer une nouvelle classe
 *     {@code CourseJpaRepository} qui implémente cette même interface. Nous pourrions alors "brancher" cette
 *     nouvelle implémentation dans l'application <b>sans changer une seule ligne de code</b> dans les classes qui
 *     utilisent le repository.</li>
 *     <li><b>Testabilité :</b> Pour les tests unitaires, nous pouvons créer une fausse implémentation (un "mock" ou
 *     un {@code InMemoryCourseRepository}) qui stocke les cours dans une simple liste en mémoire. Cela rend les
 *     tests ultra-rapides et indépendants de toute base de données réelle.</li>
 * </ul>
 * <p>Cette pratique est connue sous le nom de <b>"Programmer vers une interface, pas vers une implémentation"</b>.</p>
 */
public interface CourseRepository {

    /**
     * <h3>Pour le Débutant : Sauvegarder un Cours</h3>
     * <p>Cette méthode est le moyen de dire au système de persistance (la base de données) de prendre un objet {@link Course}
     * et de le stocker. Si un cours avec le même identifiant existe déjà, il sera mis à jour avec les nouvelles informations.
     * Sinon, un nouveau cours sera ajouté.</p>
     *
     * <h3>Pour le Futur Expert : Opération d'UPSERT</h3>
     * <p>Cette méthode implémente une opération d'<b>UPSERT</b> (UPDATE or INSERT). C'est une opération atomique
     * qui gère à la fois la création et la modification d'une entité. L'implémentation concrète (par exemple,
     * {@code CourseJdbcRepository}) devra gérer la logique pour déterminer s'il s'agit d'une insertion ou d'une mise à jour,
     * ou utiliser une commande SQL comme {@code MERGE} si la base de données la supporte.</p>
     *
     * @param course L'objet {@link Course} à sauvegarder. Il contient toutes les données du cours.
     *               Ne doit pas être {@code null}.
     * @throws RepositoryException Si une erreur survient lors de l'accès ou de la modification de la base de données.
     */
    void saveCourse(Course course);

    /**
     * <h3>Pour le Débutant : Obtenir Tous les Cours</h3>
     * <p>Cette méthode demande au système de persistance de lui donner la liste de <b>tous</b> les cours qu'il a stockés.
     * Elle retourne une collection d'objets {@link Course} que vous pouvez ensuite parcourir.</p>
     *
     * <h3>Pour le Futur Expert : Récupération de Collection et Immutabilité</h3>
     * <p>Cette méthode est responsable de la récupération d'une collection d'entités. Il est crucial que l'implémentation
     * retourne une {@link List} qui est <b>non modifiable</b> (par exemple, via {@link Collections#unmodifiableList(List)}).
     * Cela garantit que les objets {@link Course} récupérés ne peuvent pas être ajoutés ou supprimés de la liste
     * par inadvertance par le code appelant, renforçant ainsi l'intégrité des données et le principe d'immutabilité
     * des objets de domaine si ceux-ci sont des records.</p>
     *
     * @return Une {@link List} de tous les objets {@link Course} trouvés. Si aucun cours n'est trouvé,
     *         une liste vide est retournée (jamais {@code null}).
     * @throws RepositoryException Si une erreur survient lors de la lecture de la base de données.
     */
    List<Course> getAllCourses();
    static CourseRepository openCourseRepository(String databaseFile){
        return new CourseJdbcRepository(databaseFile);
    }

    void addNotes(String id, String notes);
}
