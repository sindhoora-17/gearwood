package edu.rajasekharuni.gearwood.repositories;

import edu.rajasekharuni.gearwood.entities.OrderItem;
import edu.rajasekharuni.gearwood.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProduct(Product product);
}