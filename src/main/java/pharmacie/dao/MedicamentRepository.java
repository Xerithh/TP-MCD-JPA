package pharmacie.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pharmacie.entity.Medicament;

// Cette interface sera auto-implémentée par Spring
public interface MedicamentRepository extends JpaRepository<Medicament, Integer> {
    /**
     * Trouve un médicament à partir de son nom (unique dans Medicament)
     * 
     * @return un médicament "optionnel"
     */
    Optional<Medicament> findByNom(String nom);

    /**
     * Trouve les médicaments disponibles (indisponible = false)
     * 
     * @return la liste des médicaments disponibles
     */
    List<Medicament> findByIndisponibleFalse();

    /**
     * Requête de la diapositive 51 : Liste des médicaments avec le nombre total
     * d'unités
     * commandées pour une catégorie donnée (version JPQL)
     * 
     * @param codeCategorie le code de la catégorie
     * @return liste de tableaux [nom du médicament, nombre d'unités]
     */
    @Query("""
            SELECT m.nom as nom, SUM(l.quantite) AS unites
            FROM Categorie c
            INNER JOIN c.medicaments m
            INNER JOIN LigneCommande l ON m.reference = l.medicament.reference
            WHERE c.code = :codeCategorie
            GROUP BY m.nom
            """)
    List<Object[]> medicamentsCommandesPour(@Param("codeCategorie") Integer codeCategorie);

    /**
     * Requête de la diapositive 51 : Version native SQL
     * 
     * @param codeCategorie le code de la catégorie
     * @return liste de tableaux [nom du médicament, nombre d'unités]
     */
    @Query(value = """
            SELECT m.nom as nom, SUM(l.quantite) AS unites
            FROM Ligne_Commande l
            INNER JOIN Medicament m ON l.reference_medicament = m.reference
            WHERE m.categorie_code = :codeCategorie
            GROUP BY m.nom
            """, nativeQuery = true)
    List<Object[]> medicamentsCommandesPourNative(@Param("codeCategorie") Integer codeCategorie);

    /**
     * Trouve tous les médicaments disponibles à la commande pour une catégorie
     * donnée.
     * Un médicament est disponible à la commande si :
     * - il n'est pas indisponible (indisponible = false)
     * - sa quantité en stock est >= à sa quantité en commande (unitesEnStock >=
     * unitesCommandees)
     * 
     * @param codeCategorie le code de la catégorie
     * @return la liste des médicaments disponibles à la commande
     */
    @Query("""
            SELECT m FROM Medicament m
            WHERE m.categorie.code = :codeCategorie
            AND m.indisponible = false
            AND m.unitesEnStock >= m.unitesCommandees
            """)
    List<Medicament> medicamentsDisponiblesPourCategorie(@Param("codeCategorie") Integer codeCategorie);
}
