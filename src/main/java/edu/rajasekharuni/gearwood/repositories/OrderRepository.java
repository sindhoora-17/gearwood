package edu.rajasekharuni.gearwood.repositories;

import edu.rajasekharuni.gearwood.entities.Order;
import edu.rajasekharuni.gearwood.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByOrderDateTimeDesc(User user);

    Optional<Order> findByOrderNumberAndUser(String orderNumber, User user);
}