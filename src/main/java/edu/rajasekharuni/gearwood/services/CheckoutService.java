package edu.rajasekharuni.gearwood.services;

import edu.rajasekharuni.gearwood.dtos.CheckoutDto;
import edu.rajasekharuni.gearwood.entities.CartItem;
import edu.rajasekharuni.gearwood.entities.Order;
import edu.rajasekharuni.gearwood.entities.OrderItem;
import edu.rajasekharuni.gearwood.entities.User;
import edu.rajasekharuni.gearwood.enums.OrderStatus;
import edu.rajasekharuni.gearwood.payment.gateway.PaymentProcessor;
import edu.rajasekharuni.gearwood.payment.model.PaymentRequest;
import edu.rajasekharuni.gearwood.payment.model.PaymentResult;
import edu.rajasekharuni.gearwood.payment.model.PaymentStatus;
import edu.rajasekharuni.gearwood.repositories.CartItemRepository;
import edu.rajasekharuni.gearwood.repositories.OrderRepository;
import edu.rajasekharuni.gearwood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CheckoutService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentProcessor paymentProcessor;


    @Transactional
    public String checkout(String email, CheckoutDto checkoutDto) {

        User user = userRepository.findByEmail(email).orElseThrow();

        List<CartItem> cartItems = cartService.getCartItems(email);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Your cart is empty.");
        }

        for (CartItem item : cartItems) {
            if (!item.getProduct().isActive()) {
                throw new RuntimeException(item.getProduct().getName() + " is no longer available.");
            }
        }

        PaymentRequest paymentRequest = new PaymentRequest(checkoutDto.getCardholderName(), checkoutDto.getCardNumber(), checkoutDto.getExpirationMonth(), checkoutDto.getExpirationYear(), checkoutDto.getCvv(), cartService.getTotal(email));

        PaymentResult paymentResult = paymentProcessor.processPayment(paymentRequest);

        if (paymentResult.getStatus() == PaymentStatus.DECLINED) {
            throw new RuntimeException(paymentResult.getMessage());
        }

        Order order = Order.builder().orderNumber("GW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()).user(user).orderDateTime(LocalDateTime.now()).status(OrderStatus.PAID).subtotal(cartService.getSubtotal(email)).taxAmount(cartService.getTax(email)).shippingAmount(cartService.getShipping(email)).totalAmount(cartService.getTotal(email)).transactionId(paymentResult.getTransactionId()).shippingName(checkoutDto.getShippingName()).streetAddress(checkoutDto.getStreetAddress()).address2(checkoutDto.getAddress2()).city(checkoutDto.getCity()).state(checkoutDto.getState()).zip(checkoutDto.getZip()).build();

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> OrderItem.builder().order(order).product(cartItem.getProduct()).productNameAtPurchase(cartItem.getProduct().getName()).unitPriceAtPurchase(cartItem.getUnitPrice()).quantity(cartItem.getQuantity()).lineTotal(cartItem.getLineTotal()).build()).toList();

        order.setOrderItems(orderItems);

        orderRepository.save(order);

        cartItemRepository.deleteByUser(user);

        return order.getOrderNumber();
    }
}
