package edu.rajasekharuni.gearwood.controllers;

import edu.rajasekharuni.gearwood.entities.User;
import edu.rajasekharuni.gearwood.repositories.UserRepository;
import edu.rajasekharuni.gearwood.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GlobalModelAttributes {

    private final UserRepository userRepository;
    private final CartService cartService;

    @ModelAttribute("currentUser")
    public User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return null;
        }

        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }

    @ModelAttribute("cartCount")
    public int cartCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return 0;
        }

        return cartService.getCartItems(authentication.getName()).stream().mapToInt(item -> item.getQuantity()).sum();
    }
}