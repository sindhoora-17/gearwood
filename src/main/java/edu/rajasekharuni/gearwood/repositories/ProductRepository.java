package edu.rajasekharuni.gearwood.repositories;

import edu.rajasekharuni.gearwood.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);

    List<Product> findByActiveTrue();

    List<Product> findByActiveTrueOrderByNameAsc();

    List<Product> findAllByOrderByNameAsc();
}