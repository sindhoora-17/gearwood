package edu.rajasekharuni.gearwood.controllers;

import edu.rajasekharuni.gearwood.dtos.CheckoutDto;
import edu.rajasekharuni.gearwood.services.CartService;
import edu.rajasekharuni.gearwood.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CheckoutController {

    private final CartService cartService;
    private final CheckoutService checkoutService;

    @GetMapping("/checkout")
    public String checkoutPage(Authentication authentication, Model model) {

        model.addAttribute("checkoutDto", new CheckoutDto());
        addCartSummaryToModel(authentication, model);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute("checkoutDto") CheckoutDto checkoutDto, BindingResult result, Authentication authentication, Model model) {

        if (result.hasErrors()) {
            addCartSummaryToModel(authentication, model);
            return "checkout";
        }

        try {
            String orderNumber = checkoutService.checkout(authentication.getName(), checkoutDto);
            return "redirect:/checkout/success?orderNumber=" + orderNumber;
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            addCartSummaryToModel(authentication, model);
            return "checkout";
        }
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(@RequestParam String orderNumber, Model model) {

        model.addAttribute("orderNumber", orderNumber);

        return "checkout-success";
    }

    private void addCartSummaryToModel(Authentication authentication, Model model) {

        String email = authentication.getName();

        model.addAttribute("cartItems", cartService.getCartItems(email));
        model.addAttribute("subtotal", cartService.getSubtotal(email));
        model.addAttribute("shipping", cartService.getShipping(email));
        model.addAttribute("tax", cartService.getTax(email));
        model.addAttribute("total", cartService.getTotal(email));
    }
}