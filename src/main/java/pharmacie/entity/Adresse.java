package pharmacie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Adresse {

    @Size(max = 255)
    @Column(length = 255)
    private String rue;

    @NotBlank(message = "La ville est obligatoire")
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String ville;

    @NotBlank(message = "La région est obligatoire")
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String region;

    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit être composé de 5 chiffres")
    @Column(nullable = false, length = 5)
    private String codePostal;

    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String pays;
}
