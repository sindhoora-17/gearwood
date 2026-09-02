package edu.rajasekharuni.gearwood.services;

import edu.rajasekharuni.gearwood.entities.CartItem;
import edu.rajasekharuni.gearwood.entities.Product;
import edu.rajasekharuni.gearwood.entities.User;
import edu.rajasekharuni.gearwood.repositories.CartItemRepository;
import edu.rajasekharuni.gearwood.repositories.ProductRepository;
import edu.rajasekharuni.gearwood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public void addToCart(String email, String productCode, Integer quantity) {

        User user = userRepository.findByEmail(email).orElseThrow();

        Product product = productRepository.findByProductCode(productCode).orElseThrow();

        if (!product.isActive()) {
            throw new RuntimeException("This product is not available.");
        }

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product).orElse(null);

        if (cartItem == null) {
            cartItem = CartItem.builder().user(user).product(product).quantity(quantity).unitPrice(product.getPrice()).build();
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return cartItemRepository.findByUser(user);
    }

    public BigDecimal getSubtotal(String email) {
        return getCartItems(email).stream().map(CartItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public void removeItem(Long cartItemId) {

        cartItemRepository.deleteById(cartItemId);
    }

    public void updateQuantity(Long cartItemId, Integer quantity) {

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow();

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    public BigDecimal getShipping(String email) {

        BigDecimal subtotal = getSubtotal(email);

        if (subtotal.compareTo(new BigDecimal("75.00")) >= 0 || subtotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal("9.99").setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getTax(String email) {

        BigDecimal subtotal = getSubtotal(email);

        return subtotal.multiply(new BigDecimal("0.0825")).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal(String email) {

        return getSubtotal(email).add(getShipping(email)).add(getTax(email)).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public boolean hasInactiveItems(String email) {
        return getCartItems(email).stream().anyMatch(item -> !item.getProduct().isActive());
    }
}