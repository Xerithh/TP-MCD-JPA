package pharmacie.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pharmacie.entity.Commande;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    List<Commande> findByDateSaisieAfter(LocalDate date);

    /**
     * Calcule le nombre total d'articles déjà commandés par un dispensaire.
     * Une commande est considérée comme "envoyée" si dateExpedition n'est pas null.
     * 
     * @param codeDispensaire le code du dispensaire
     * @return le nombre total d'articles commandés et envoyés
     */
    @Query("""
            SELECT COALESCE(SUM(l.quantite), 0)
            FROM Commande c
            INNER JOIN c.lignesCommande l
            WHERE c.dispensaire.code = :codeDispensaire
            AND c.dateExpedition IS NOT NULL
            """)
    Long nombreArticlesCommandesPar(@Param("codeDispensaire") Integer codeDispensaire);

    /**
     * Trouve toutes les commandes en cours pour un dispensaire donné.
     * Une commande est "en cours" si sa date d'expédition (dateExpedition) n'est
     * pas renseignée (null).
     * 
     * @param codeDispensaire le code du dispensaire
     * @return la liste des commandes en cours
     */
    @Query("""
            SELECT c FROM Commande c
            WHERE c.dispensaire.code = :codeDispensaire
            AND c.dateExpedition IS NULL
            """)
    List<Commande> commandesEnCoursPour(@Param("codeDispensaire") Integer codeDispensaire);
}
