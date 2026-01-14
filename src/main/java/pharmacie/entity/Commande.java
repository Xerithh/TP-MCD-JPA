package pharmacie.entity;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // la clé est auto-générée par la BD, On ne veut pas de "setter"
    private Integer numero;

    @NonNull
    @NotNull
    @PastOrPresent(message = "La date de saisie ne peut pas être dans le futur")
    @Column(nullable = false)
    private LocalDate dateSaisie;

    @Column
    private LocalDate dateExpedition;

    @ManyToOne(optional = false)
    @NonNull
    @ToString.Exclude
    private Dispensaire dispensaire;

    @ToString.Exclude
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignesCommande = new LinkedList<>();
}
