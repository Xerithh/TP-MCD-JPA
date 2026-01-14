package pharmacie.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LigneCommande {

    @EmbeddedId
    private LigneCommandeId id;

    @ManyToOne
    @MapsId("numeroCommande")
    @JoinColumn(name = "numero_commande")
    @ToString.Exclude
    private Commande commande;

    @ManyToOne
    @MapsId("referenceMedicament")
    @JoinColumn(name = "reference_medicament")
    @ToString.Exclude
    private Medicament medicament;

    @Positive(message = "La quantité doit être positive")
    @Column(nullable = false)
    private int quantite;

    /**
     * Classe interne pour la clé composite de LigneCommande
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class LigneCommandeId implements Serializable {
        private Integer numeroCommande;
        private Integer referenceMedicament;
    }
}
