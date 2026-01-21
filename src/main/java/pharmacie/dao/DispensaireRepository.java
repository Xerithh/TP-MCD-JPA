package pharmacie.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pharmacie.entity.Dispensaire;

public interface DispensaireRepository extends JpaRepository<Dispensaire, Integer> {

    List<Dispensaire> findByAdresseRegion(String region);
}
