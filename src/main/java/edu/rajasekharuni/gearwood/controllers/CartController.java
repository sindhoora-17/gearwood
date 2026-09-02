package edu.rajasekharuni.gearwood.controllers;

import edu.rajasekharuni.gearwood.entities.CartItem;
import edu.rajasekharuni.gearwood.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart/add/{productCode}")
    public String addToCart(@PathVariable String productCode, @RequestParam Integer quantity, Authentication authentication) {

        cartService.addToCart(authentication.getName(), productCode, quantity);

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Authentication authentication, Model model) {

        List<CartItem> cartItems = cartService.getCartItems(authentication.getName());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", cartService.getSubtotal(authentication.getName()));
        model.addAttribute("shipping", cartService.getShipping(authentication.getName()));

        model.addAttribute("tax", cartService.getTax(authentication.getName()));

        model.addAttribute("total", cartService.getTotal(authentication.getName()));
        model.addAttribute("hasInactiveItems", cartService.hasInactiveItems(authentication.getName()));

        return "cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeItem(@PathVariable Long id) {

        cartService.removeItem(id);

        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {

        cartService.updateQuantity(id, quantity);

        return "redirect:/cart";
    }
}