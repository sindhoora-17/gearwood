package edu.rajasekharuni.gearwood.services;

import edu.rajasekharuni.gearwood.entities.Order;
import edu.rajasekharuni.gearwood.entities.User;
import edu.rajasekharuni.gearwood.repositories.OrderRepository;
import edu.rajasekharuni.gearwood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public List<Order> getOrdersForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return orderRepository.findByUserOrderByOrderDateTimeDesc(user);
    }

    public Order getOrder(String email, String orderNumber) {

        User user = userRepository.findByEmail(email).orElseThrow();

        return orderRepository.findByOrderNumberAndUser(orderNumber, user).orElseThrow();
    }
}