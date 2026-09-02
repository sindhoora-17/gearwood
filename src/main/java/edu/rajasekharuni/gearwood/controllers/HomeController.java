package edu.rajasekharuni.gearwood.controllers;

import edu.rajasekharuni.gearwood.dtos.RegisterDto;
import edu.rajasekharuni.gearwood.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HomeController {
    private final AuthService authService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("registerDto", new RegisterDto());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto, BindingResult result) {

        if (result.hasErrors()) {
            return "register";
        }

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {

            result.rejectValue("confirmPassword", "error.registerDto", "Passwords do not match");

            return "register";
        }

        try {
            authService.registerUser(registerDto);
        } catch (RuntimeException e) {

            result.rejectValue("email", "error.registerDto", e.getMessage());

            return "register";
        }

        return "redirect:/login";
    }

}