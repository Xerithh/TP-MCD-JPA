package pharmacie.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pharmacie.entity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RepositoryCustomMethodsTest {

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;
    @Autowired
    private DispensaireRepository dispensaireRepository;
    @Autowired
    private CommandeRepository commandeRepository;

    @Test // Ce test se base uniquement sur les données définies dans data.sql
    public void testMedicamentCustomMethods() {
        Medicament indisponible = medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow();
        Medicament disponible = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();

        // Trouve tous les médicaments disponibles
        List<Medicament> disponibles = medicamentRepository.findByIndisponibleFalse();

        assertTrue(disponibles.contains(disponible));
        assertFalse(disponibles.contains(indisponible));
        assertFalse(disponibles.isEmpty());
    }

    @Test // Ce test crée les enregistrements nécessaires
    public void testCategorieCustomMethods() {
        Categorie c1 = new Categorie();
        c1.setLibelle("AnalgesiquesTest");
        categorieRepository.save(c1);

        Categorie c2 = new Categorie();
        c2.setLibelle("AntibiotiquesTest");
        categorieRepository.save(c2);

        // findByLibelle
        Categorie found = categorieRepository.findByLibelle("AnalgesiquesTest");
        assertNotNull(found);
        assertEquals("AnalgesiquesTest", found.getLibelle());

        // findByLibelleContaining
        List<Categorie> list = categorieRepository.findByLibelleContaining("iquesTest");
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AntibiotiquesTest")));
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AnalgesiquesTest")));
    }

    @Test // Test pour la méthode findByRegion du DispensaireRepository
    public void testDispensaireCustomMethods() {
        // Recherche des dispensaires dans la région de Dakar
        List<Dispensaire> dispensairesDakar = dispensaireRepository.findByRegion("Dakar");

        // Vérification qu'on a trouvé 2 dispensaires dans la région de Dakar (selon
        // data.sql)
        assertEquals(2, dispensairesDakar.size());

        // Vérification que les dispensaires trouvés appartiennent bien à la région
        // Dakar
        assertTrue(dispensairesDakar.stream().allMatch(d -> d.getAdresse().getRegion().equals("Dakar")));

        // Recherche des dispensaires dans la région de Thiès
        List<Dispensaire> dispensairesThies = dispensaireRepository.findByRegion("Thiès");
        assertEquals(1, dispensairesThies.size());
        assertEquals("Dispensaire de Thiès", dispensairesThies.get(0).getNom());
        assertEquals("Thiès", dispensairesThies.get(0).getAdresse().getRegion());
    }

    @Test // Test pour la méthode findByDateSaisieAfter du CommandeRepository
    public void testCommandeCustomMethods() {
        // Recherche des commandes saisies après le 1er février 2024
        LocalDate date = LocalDate.of(2024, 2, 1);
        List<Commande> commandesApres = commandeRepository.findByDateSaisieAfter(date);

        // Vérification qu'on a trouvé des commandes
        assertFalse(commandesApres.isEmpty());

        // Vérification que toutes les commandes trouvées ont été saisies après la date
        // spécifiée
        assertTrue(commandesApres.stream().allMatch(c -> c.getDateSaisie().isAfter(date)));

        // D'après data.sql, on devrait avoir 5 commandes après le 01/02/2024
        // (10/02, 15/02, 01/03, 10/03, 15/03)
        assertEquals(5, commandesApres.size());

        // Test avec une date plus récente
        LocalDate dateMars = LocalDate.of(2024, 3, 1);
        List<Commande> commandesApresMars = commandeRepository.findByDateSaisieAfter(dateMars);

        // On devrait avoir 2 commandes après le 01/03/2024 (10/03 et 15/03)
        assertEquals(2, commandesApresMars.size());
    }

}
