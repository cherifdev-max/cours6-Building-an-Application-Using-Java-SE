package com.pluralsight.courseinfo.repository;

import java.sql.SQLException;

/**
 * <h2>Documentation de la classe {@code RepositoryException}</h2>
 *
 * <h3>Pour le Débutant : Qu'est-ce qu'une exception personnalisée ?</h3>
 * <p>Java fournit de nombreuses exceptions de base (comme {@code IOException}, {@code NullPointerException}, etc.).
 * Cependant, il est souvent utile de créer nos propres classes d'exception pour représenter des erreurs
 * spécifiques à notre application. C'est exactement ce que nous faisons ici.</p>
 * <p>{@code RepositoryException} est une exception qui sera "levée" (lancée) chaque fois qu'une opération
 * liée à la base de données échoue pour une raison quelconque.</p>
 *
 * <h3>Pour le Futur Expert : Le "Pourquoi" de cette exception (Abstraction et Encapsulation)</h3>
 * <p>La création d'exceptions personnalisées est une autre facette du principe d'<b>abstraction</b>, tout comme
 * l'interface {@code CourseRepository}. Le but est de <b>cacher les détails d'implémentation</b>.</p>
 *
 * <p>Notre {@code CourseJdbcRepository} utilise JDBC, qui peut lancer des {@link SQLException}. Une {@code SQLException}
 * est une "checked exception" très spécifique à la technologie JDBC. Si nous laissions cette exception se propager
 * dans toute notre application, cela signifierait que toutes les classes qui appellent notre repository devraient
 * savoir que nous utilisons JDBC et gérer cette {@code SQLException}. Cela créerait un couplage fort.</p>
 *
 * <p><b>En "enveloppant" (wrapping) la {@code SQLException} dans notre propre {@code RepositoryException}, nous obtenons plusieurs avantages :</b></p>
 * <ul>
 *     <li><b>Abstraction :</b> Les clients du repository n'ont plus besoin de savoir quelle technologie de persistance est
 *     utilisée. Ils attrapent une {@code RepositoryException}, un terme générique qui signifie simplement "quelque chose
 *     s'est mal passé dans la couche de persistance".</li>
 *     <li><b>Chacngement de technologie facilité :</b> Si demain nous passons à JPA, qui peut lancer des
 *  *     {@code PersistenceExeption}, nous n'aurons qu'à modifier notre repository pour qu'il enveloppe la
 *     {@code PersistenceException} dans une {@code RepositoryException}. Le reste de l'application n'est pas impacté.</li>
 *     <li><b>Choix du type d'exception :</b> Nous avons choisi de faire de {@code RepositoryException} une
 *     <b>"unchecked exception"</b> (en héritant de {@link RuntimeException}). C'est un choix de conception important.
 *     Cela signifie que les appelants ne sont pas <i>forcés</i> par le compilateur à l'attraper. On fait ce choix quand
 *     on considère que l'erreur est souvent irrécupérable (ex: la base de données est hors service) et qu'il est plus
 *     propre de laisser l'application s'arrêter que de forcer chaque méthode à gérer une erreur qu'elle ne peut pas résoudre.</li>
 * </ul>
 */
public class RepositoryException extends RuntimeException {
    /**
     * <h3>Pour le Débutant : Construire une Exception</h3>
     * <p>Ce constructeur crée une nouvelle {@code RepositoryException}. Il prend deux informations importantes :
     * un message pour les humains, et l'exception originale qui a causé le problème.</p>
     *
     * <h3>Pour le Futur Expert : Le Chaînage d'Exceptions (Exception Chaining)</h3>
     * <p>Ce constructeur est un exemple parfait de "chaînage d'exceptions". En passant l'exception originale
     * (la {@code cause}) au constructeur parent ({@code super(message, cause)}), on ne perd pas la pile d'appels
     * (stack trace) de l'erreur initiale. C'est <b>crucial</b> pour le débogage. Quand on lira les logs d'erreur,
     * on verra notre {@code RepositoryException}, mais aussi la {@code SQLException} originale avec tous les détails
     * techniques qui expliquent pourquoi la base de données a échoué. Ne jamais "avaler" une exception sans la chainer !</p>
     *
     * @param message Un message clair et lisible par un humain, décrivant le contexte de l'erreur.
     *                (Ex: "Impossible de sauvegarder le cours X").
     * @param cause   L'exception originale (la "cause racine") qui a provoqué cette erreur. En la stockant,
     *                on ne perd pas d'informations précieuses pour le débogage.
     */
    public RepositoryException(String message, SQLException cause) {
        super(message, cause);
    }
}
