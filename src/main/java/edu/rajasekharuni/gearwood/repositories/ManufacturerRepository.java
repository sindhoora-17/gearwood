package edu.rajasekharuni.gearwood.repositories;

import edu.rajasekharuni.gearwood.entities.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    Optional<Manufacturer> findByName(String name);
}