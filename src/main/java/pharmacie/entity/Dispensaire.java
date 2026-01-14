package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Dispensaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // la clé est auto-générée par la BD, On ne veut pas de "setter"
    private Integer code;

    @NonNull
    @NotBlank
    @Size(min = 1, max = 100)
    @Column(unique = true, nullable = false, length = 100)
    private String nom;

    @Pattern(regexp = "^[0-9 +()-]*$", message = "Le numéro de téléphone doit contenir uniquement des chiffres, espaces et caractères spéciaux")
    @Size(max = 20)
    @Column(length = 20)
    private String telephone;

    @Embedded
    private Adresse adresse;

    @ToString.Exclude
    @OneToMany(mappedBy = "dispensaire", cascade = CascadeType.ALL)
    private List<Commande> commandes = new LinkedList<>();
}
