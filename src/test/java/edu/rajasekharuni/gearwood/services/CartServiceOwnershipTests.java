package edu.rajasekharuni.gearwood.services;

import edu.rajasekharuni.gearwood.entities.CartItem;
import edu.rajasekharuni.gearwood.entities.User;
import edu.rajasekharuni.gearwood.repositories.CartItemRepository;
import edu.rajasekharuni.gearwood.repositories.ProductRepository;
import edu.rajasekharuni.gearwood.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceOwnershipTests {
    @Mock CartItemRepository cartItems;
    @Mock ProductRepository products;
    @Mock UserRepository users;
    private CartService service;
    private User owner;

    @BeforeEach
    void setup() {
        service = new CartService(cartItems, products, users);
        owner = new User();
        when(users.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
    }

    @Test
    void cannotRemoveAnotherUsersCartItem() {
        when(cartItems.findByIdAndUser(42L, owner)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.removeItem("owner@example.com", 42L));
        verify(cartItems, never()).delete(any());
        verify(cartItems, never()).deleteById(anyLong());
    }

    @Test
    void cannotUpdateAnotherUsersCartItem() {
        when(cartItems.findByIdAndUser(42L, owner)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.updateQuantity("owner@example.com", 42L, 2));
        verify(cartItems, never()).save(any());
    }

    @Test
    void ownerCanRemoveTheirCartItem() {
        CartItem item = new CartItem();
        when(cartItems.findByIdAndUser(42L, owner)).thenReturn(Optional.of(item));
        service.removeItem("owner@example.com", 42L);
        verify(cartItems).delete(item);
    }
}
