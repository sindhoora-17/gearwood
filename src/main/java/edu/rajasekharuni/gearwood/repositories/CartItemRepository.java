package edu.rajasekharuni.gearwood.repositories;

import edu.rajasekharuni.gearwood.entities.CartItem;
import edu.rajasekharuni.gearwood.entities.Product;
import edu.rajasekharuni.gearwood.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    void deleteByUser(User user);

    boolean existsByProduct(Product product);
}