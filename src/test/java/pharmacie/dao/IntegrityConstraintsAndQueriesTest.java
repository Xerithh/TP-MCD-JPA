package pharmacie.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import pharmacie.entity.*;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class IntegrityConstraintsAndQueriesTest {

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private DispensaireRepository dispensaireRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private EntityManager entityManager;

    // Méthode helper pour créer une adresse complète
    private Adresse creerAdresse(String ville) {
        Adresse adresse = new Adresse();
        adresse.setRue("1 rue Test");
        adresse.setVille(ville);
        adresse.setRegion("Île-de-France");
        adresse.setCodePostal("75001");
        adresse.setPays("France");
        return adresse;
    }

    // =========================================
    // Tests des contraintes d'intégrité
    // =========================================

    @Test
    public void testMedicamentDoitAvoirUneCategorie() {
        // Tenter de créer un médicament sans catégorie doit échouer
        Medicament medicament = new Medicament();
        medicament.setNom("Medicament Sans Categorie");

        // Sans catégorie, la validation JPA doit échouer
        assertThrows(Exception.class, () -> {
            medicamentRepository.saveAndFlush(medicament);
        });
    }

    @Test
    public void testPeutSupprimerCategorieSansMedicaments() {
        // Créer une catégorie sans médicaments
        Categorie categorie = new Categorie("Catégorie Vide Test 1");
        categorie.setDescription("Une catégorie sans médicaments");
        categorieRepository.saveAndFlush(categorie);

        Integer code = categorie.getCode();
        assertNotNull(code);

        // Supprimer la catégorie doit réussir
        categorieRepository.deleteById(code);
        categorieRepository.flush();

        // Vérifier que la catégorie a été supprimée
        assertFalse(categorieRepository.findById(code).isPresent());
    }

    @Test
    public void testNePeutPasSupprimerCategorieAvecMedicaments() {
        // Créer une catégorie avec un médicament
        Categorie categorie = new Categorie("Catégorie Avec Médicament Test");
        categorie = categorieRepository.saveAndFlush(categorie);

        Integer code = categorie.getCode();

        Medicament medicament = new Medicament("Doliprane 1000 Test", categorie);
        medicament.setPrixUnitaire(new BigDecimal("5.50"));
        medicamentRepository.saveAndFlush(medicament);

        Integer refMed = medicament.getReference();

        // Clear le contexte de persistance pour forcer le rechargement
        entityManager.clear();

        // Tenter de supprimer la catégorie doit échouer (contrainte d'intégrité
        // référentielle)
        assertThrows(DataIntegrityViolationException.class, () -> {
            categorieRepository.deleteById(code);
            categorieRepository.flush();
        });
    }

    @Test
    public void testSupprimerCommandeSupprimeLignes() {
        // Créer un dispensaire
        Dispensaire dispensaire = new Dispensaire("Dispensaire Test Lignes");
        dispensaire.setTelephone("0123456789");
        dispensaire.setAdresse(creerAdresse("Paris"));
        dispensaireRepository.saveAndFlush(dispensaire);

        // Créer une catégorie et un médicament
        Categorie categorie = new Categorie("Analgésiques Test");
        categorieRepository.saveAndFlush(categorie);

        Medicament medicament = new Medicament("Aspirine Test", categorie);
        medicament.setPrixUnitaire(new BigDecimal("3.50"));
        medicamentRepository.saveAndFlush(medicament);

        // Créer une commande
        Commande commande = new Commande(LocalDate.now(), dispensaire);
        commandeRepository.saveAndFlush(commande);

        // Ajouter une ligne de commande
        LigneCommande ligne = new LigneCommande();
        ligne.setId(new LigneCommande.LigneCommandeId(commande.getNumero(), medicament.getReference()));
        ligne.setCommande(commande);
        ligne.setMedicament(medicament);
        ligne.setQuantite(10);
        commande.getLignesCommande().add(ligne);
        commandeRepository.saveAndFlush(commande);

        Integer numeroCommande = commande.getNumero();
        assertNotNull(numeroCommande);
        assertEquals(1, commande.getLignesCommande().size());

        // Supprimer la commande
        commandeRepository.deleteById(numeroCommande);
        commandeRepository.flush();

        // Vérifier que la commande et ses lignes ont été supprimées
        assertFalse(commandeRepository.findById(numeroCommande).isPresent());
    }

    @Test
    public void testSupprimerDispensaireSupprimeSesCommandes() {
        // Créer un dispensaire avec des commandes
        Dispensaire dispensaire = new Dispensaire("Dispensaire à Supprimer Test");
        dispensaire.setTelephone("0987654321");
        dispensaire.setAdresse(creerAdresse("Lyon"));
        dispensaire = dispensaireRepository.saveAndFlush(dispensaire);

        Integer codeDispensaire = dispensaire.getCode();

        Commande commande1 = new Commande(LocalDate.now(), dispensaire);
        Commande commande2 = new Commande(LocalDate.now().minusDays(1), dispensaire);
        commandeRepository.saveAndFlush(commande1);
        commandeRepository.saveAndFlush(commande2);

        Integer numeroCommande1 = commande1.getNumero();
        Integer numeroCommande2 = commande2.getNumero();

        // Vérifier que les commandes existent
        assertTrue(commandeRepository.findById(numeroCommande1).isPresent());
        assertTrue(commandeRepository.findById(numeroCommande2).isPresent());

        // Clear le contexte de persistance
        entityManager.clear();

        // Supprimer le dispensaire
        dispensaireRepository.deleteById(codeDispensaire);
        dispensaireRepository.flush();

        // Vérifier que le dispensaire et ses commandes ont été supprimés
        assertFalse(dispensaireRepository.findById(codeDispensaire).isPresent());
        assertFalse(commandeRepository.findById(numeroCommande1).isPresent());
        assertFalse(commandeRepository.findById(numeroCommande2).isPresent());
    }

    // =========================================
    // Tests des requêtes personnalisées
    // =========================================

    @Test
    public void testMedicamentsCommandesPour() {
        // Créer une catégorie
        Categorie categorie = new Categorie("Antibiotiques Test Requête");
        categorieRepository.saveAndFlush(categorie);

        // Créer des médicaments
        Medicament med1 = new Medicament("Amoxicilline Test", categorie);
        med1.setPrixUnitaire(new BigDecimal("8.50"));
        Medicament med2 = new Medicament("Azithromycine Test", categorie);
        med2.setPrixUnitaire(new BigDecimal("12.00"));
        medicamentRepository.saveAndFlush(med1);
        medicamentRepository.saveAndFlush(med2);

        // Créer un dispensaire et une commande
        Dispensaire dispensaire = new Dispensaire("Dispensaire Test Requêtes");
        dispensaire.setAdresse(creerAdresse("Marseille"));
        dispensaireRepository.saveAndFlush(dispensaire);

        Commande commande1 = new Commande(LocalDate.now(), dispensaire);
        commandeRepository.saveAndFlush(commande1);

        // Ajouter des lignes de commande
        LigneCommande ligne1 = new LigneCommande();
        ligne1.setId(new LigneCommande.LigneCommandeId(commande1.getNumero(), med1.getReference()));
        ligne1.setCommande(commande1);
        ligne1.setMedicament(med1);
        ligne1.setQuantite(10);
        commande1.getLignesCommande().add(ligne1);

        LigneCommande ligne2 = new LigneCommande();
        ligne2.setId(new LigneCommande.LigneCommandeId(commande1.getNumero(), med2.getReference()));
        ligne2.setCommande(commande1);
        ligne2.setMedicament(med2);
        ligne2.setQuantite(5);
        commande1.getLignesCommande().add(ligne2);

        commandeRepository.saveAndFlush(commande1);

        // Créer une autre commande pour le même médicament
        Commande commande2 = new Commande(LocalDate.now().minusDays(1), dispensaire);
        commandeRepository.saveAndFlush(commande2);

        LigneCommande ligne3 = new LigneCommande();
        ligne3.setId(new LigneCommande.LigneCommandeId(commande2.getNumero(), med1.getReference()));
        ligne3.setCommande(commande2);
        ligne3.setMedicament(med1);
        ligne3.setQuantite(20);
        commande2.getLignesCommande().add(ligne3);

        commandeRepository.saveAndFlush(commande2);

        // Tester la requête JPQL
        List<Object[]> resultats = medicamentRepository.medicamentsCommandesPour(categorie.getCode());

        assertNotNull(resultats);
        assertEquals(2, resultats.size()); // 2 médicaments différents

        // Vérifier les résultats
        boolean amoxicillineTrouvee = false;
        boolean azithromycineTrouvee = false;

        for (Object[] row : resultats) {
            String nom = (String) row[0];
            Long quantite = ((Number) row[1]).longValue();

            if (nom.equals("Amoxicilline Test")) {
                assertEquals(30L, quantite); // 10 + 20
                amoxicillineTrouvee = true;
            } else if (nom.equals("Azithromycine Test")) {
                assertEquals(5L, quantite);
                azithromycineTrouvee = true;
            }
        }

        assertTrue(amoxicillineTrouvee);
        assertTrue(azithromycineTrouvee);
    }

    @Test
    public void testMedicamentsDisponiblesPourCategorie() {
        // Créer une catégorie
        Categorie categorie = new Categorie("Vitamines Test");
        categorieRepository.saveAndFlush(categorie);

        // Créer des médicaments avec différents états
        // Médicament disponible (non indisponible, stock >= commandé)
        Medicament medDispo = new Medicament("Vitamine C Test", categorie);
        medDispo.setPrixUnitaire(new BigDecimal("6.00"));
        medDispo.setUnitesEnStock(100);
        medDispo.setUnitesCommandees(50);
        medDispo.setIndisponible(false);

        // Médicament indisponible
        Medicament medIndispo = new Medicament("Vitamine D Test", categorie);
        medIndispo.setPrixUnitaire(new BigDecimal("7.50"));
        medIndispo.setUnitesEnStock(100);
        medIndispo.setUnitesCommandees(50);
        medIndispo.setIndisponible(true);

        // Médicament avec stock insuffisant
        Medicament medStockFaible = new Medicament("Vitamine E Test", categorie);
        medStockFaible.setPrixUnitaire(new BigDecimal("8.00"));
        medStockFaible.setUnitesEnStock(30);
        medStockFaible.setUnitesCommandees(50);
        medStockFaible.setIndisponible(false);

        // Médicament disponible avec stock égal aux commandes
        Medicament medLimite = new Medicament("Vitamine K Test", categorie);
        medLimite.setPrixUnitaire(new BigDecimal("5.50"));
        medLimite.setUnitesEnStock(50);
        medLimite.setUnitesCommandees(50);
        medLimite.setIndisponible(false);

        medicamentRepository.saveAndFlush(medDispo);
        medicamentRepository.saveAndFlush(medIndispo);
        medicamentRepository.saveAndFlush(medStockFaible);
        medicamentRepository.saveAndFlush(medLimite);

        // Tester la requête
        List<Medicament> disponibles = medicamentRepository.medicamentsDisponiblesPourCategorie(categorie.getCode());

        assertNotNull(disponibles);
        assertEquals(2, disponibles.size()); // medDispo et medLimite

        assertTrue(disponibles.stream().anyMatch(m -> m.getNom().equals("Vitamine C Test")));
        assertTrue(disponibles.stream().anyMatch(m -> m.getNom().equals("Vitamine K Test")));
        assertFalse(disponibles.stream().anyMatch(m -> m.getNom().equals("Vitamine D Test")));
        assertFalse(disponibles.stream().anyMatch(m -> m.getNom().equals("Vitamine E Test")));
    }

    @Test
    public void testNombreArticlesCommandesPar() {
        // Créer un dispensaire
        Dispensaire dispensaire = new Dispensaire("Dispensaire Comptage Test");
        dispensaire.setAdresse(creerAdresse("Toulouse"));
        dispensaireRepository.saveAndFlush(dispensaire);

        // Créer une catégorie et des médicaments
        Categorie categorie = new Categorie("Antalgiques Test");
        categorieRepository.saveAndFlush(categorie);

        Medicament med1 = new Medicament("Paracétamol Test", categorie);
        med1.setPrixUnitaire(new BigDecimal("4.50"));
        Medicament med2 = new Medicament("Ibuprofène Test", categorie);
        med2.setPrixUnitaire(new BigDecimal("6.00"));
        medicamentRepository.saveAndFlush(med1);
        medicamentRepository.saveAndFlush(med2);

        // Créer des commandes envoyées
        Commande commande1 = new Commande(LocalDate.now().minusDays(5), dispensaire);
        commande1.setDateExpedition(LocalDate.now().minusDays(3)); // Envoyée
        commandeRepository.saveAndFlush(commande1);

        LigneCommande ligne1 = new LigneCommande();
        ligne1.setId(new LigneCommande.LigneCommandeId(commande1.getNumero(), med1.getReference()));
        ligne1.setCommande(commande1);
        ligne1.setMedicament(med1);
        ligne1.setQuantite(15);
        commande1.getLignesCommande().add(ligne1);

        LigneCommande ligne2 = new LigneCommande();
        ligne2.setId(new LigneCommande.LigneCommandeId(commande1.getNumero(), med2.getReference()));
        ligne2.setCommande(commande1);
        ligne2.setMedicament(med2);
        ligne2.setQuantite(25);
        commande1.getLignesCommande().add(ligne2);

        commandeRepository.saveAndFlush(commande1);

        // Créer une autre commande envoyée
        Commande commande2 = new Commande(LocalDate.now().minusDays(10), dispensaire);
        commande2.setDateExpedition(LocalDate.now().minusDays(8));
        commandeRepository.saveAndFlush(commande2);

        LigneCommande ligne3 = new LigneCommande();
        ligne3.setId(new LigneCommande.LigneCommandeId(commande2.getNumero(), med1.getReference()));
        ligne3.setCommande(commande2);
        ligne3.setMedicament(med1);
        ligne3.setQuantite(10);
        commande2.getLignesCommande().add(ligne3);

        commandeRepository.saveAndFlush(commande2);

        // Créer une commande non envoyée (ne doit pas être comptée)
        Commande commande3 = new Commande(LocalDate.now(), dispensaire);
        commande3.setDateExpedition(null); // Non envoyée
        commandeRepository.saveAndFlush(commande3);

        LigneCommande ligne4 = new LigneCommande();
        ligne4.setId(new LigneCommande.LigneCommandeId(commande3.getNumero(), med1.getReference()));
        ligne4.setCommande(commande3);
        ligne4.setMedicament(med1);
        ligne4.setQuantite(100); // Cette quantité ne doit pas être comptée
        commande3.getLignesCommande().add(ligne4);

        commandeRepository.saveAndFlush(commande3);

        // Tester la requête
        Long nombreArticles = commandeRepository.nombreArticlesCommandesPar(dispensaire.getCode());

        assertNotNull(nombreArticles);
        assertEquals(50L, nombreArticles); // 15 + 25 + 10 (commande3 non comptée)
    }

    @Test
    public void testCommandesEnCoursPour() {
        // Créer un dispensaire
        Dispensaire dispensaire = new Dispensaire("Dispensaire Commandes En Cours Test");
        dispensaire.setAdresse(creerAdresse("Nice"));
        dispensaireRepository.saveAndFlush(dispensaire);

        // Créer des commandes en cours (dateExpedition = null)
        Commande commandeEnCours1 = new Commande(LocalDate.now(), dispensaire);
        commandeEnCours1.setDateExpedition(null);

        Commande commandeEnCours2 = new Commande(LocalDate.now().minusDays(2), dispensaire);
        commandeEnCours2.setDateExpedition(null);

        // Créer des commandes envoyées (ne doivent pas apparaître)
        Commande commandeEnvoyee1 = new Commande(LocalDate.now().minusDays(5), dispensaire);
        commandeEnvoyee1.setDateExpedition(LocalDate.now().minusDays(3));

        Commande commandeEnvoyee2 = new Commande(LocalDate.now().minusDays(10), dispensaire);
        commandeEnvoyee2.setDateExpedition(LocalDate.now().minusDays(7));

        commandeRepository.saveAndFlush(commandeEnCours1);
        commandeRepository.saveAndFlush(commandeEnCours2);
        commandeRepository.saveAndFlush(commandeEnvoyee1);
        commandeRepository.saveAndFlush(commandeEnvoyee2);

        // Tester la requête
        List<Commande> commandesEnCours = commandeRepository.commandesEnCoursPour(dispensaire.getCode());

        assertNotNull(commandesEnCours);
        assertEquals(2, commandesEnCours.size());

        // Vérifier que seules les commandes en cours sont retournées
        assertTrue(commandesEnCours.stream().allMatch(c -> c.getDateExpedition() == null));
        assertTrue(commandesEnCours.stream().anyMatch(c -> c.getNumero().equals(commandeEnCours1.getNumero())));
        assertTrue(commandesEnCours.stream().anyMatch(c -> c.getNumero().equals(commandeEnCours2.getNumero())));
    }

    @Test
    public void testCommandesEnCoursDispensaireSansCommandes() {
        // Créer un dispensaire sans commandes
        Dispensaire dispensaire = new Dispensaire("Dispensaire Sans Commandes Test");
        dispensaire.setAdresse(creerAdresse("Nantes"));
        dispensaireRepository.saveAndFlush(dispensaire);

        // Tester la requête
        List<Commande> commandesEnCours = commandeRepository.commandesEnCoursPour(dispensaire.getCode());

        assertNotNull(commandesEnCours);
        assertTrue(commandesEnCours.isEmpty());
    }

    @Test
    public void testNombreArticlesDispensaireSansCommandes() {
        // Créer un dispensaire sans commandes
        Dispensaire dispensaire = new Dispensaire("Dispensaire Sans Commandes 2 Test");
        dispensaire.setAdresse(creerAdresse("Bordeaux"));
        dispensaireRepository.saveAndFlush(dispensaire);

        // Tester la requête
        Long nombreArticles = commandeRepository.nombreArticlesCommandesPar(dispensaire.getCode());

        assertNotNull(nombreArticles);
        assertEquals(0L, nombreArticles);
    }
}
