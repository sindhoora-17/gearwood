package edu.rajasekharuni.gearwood.controllers;

import edu.rajasekharuni.gearwood.entities.User;
import edu.rajasekharuni.gearwood.repositories.UserRepository;
import edu.rajasekharuni.gearwood.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AccountController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;

    @GetMapping("/account")
    public String account(Authentication authentication, Model model) {

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getOrdersForUser(authentication.getName()));

        return "account";
    }

    @GetMapping("/account/edit")
    public String editProfile(Authentication authentication, Model model) {

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        model.addAttribute("user", user);

        return "edit-profile";
    }

    @PostMapping("/account/edit")
    public String updateProfile(Authentication authentication, @RequestParam String firstName, @RequestParam String lastName) {

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        user.setFirstName(firstName);
        user.setLastName(lastName);

        userRepository.save(user);

        return "redirect:/account";
    }

    @GetMapping("/account/change-password")
    public String changePasswordPage() {
        return "change-password";
    }

    @PostMapping("/account/change-password")
    public String changePassword(Authentication authentication, @RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmNewPassword, Model model) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect");
            return "change-password";
        }

        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            return "change-password";
        }

        if (!newPassword.equals(confirmNewPassword)) {model.addAttribute("error", "New password and confirm password do not match");
            return "change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        model.addAttribute("success", "Password updated successfully");

        return "change-password";
    }

    @GetMapping("/account/orders/{orderNumber}")
    public String orderDetails(@PathVariable String orderNumber, Authentication authentication, Model model) {

        model.addAttribute("order", orderService.getOrder(authentication.getName(), orderNumber));
        return "order-details";
    }

    @GetMapping("/account/orders")
    public String orderHistory(Authentication authentication, Model model) {
        model.addAttribute("orders", orderService.getOrdersForUser(authentication.getName()));
        return "order-history";
    }
}