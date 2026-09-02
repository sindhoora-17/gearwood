package edu.rajasekharuni.gearwood.controllers;

import edu.rajasekharuni.gearwood.entities.Product;
import edu.rajasekharuni.gearwood.enums.Category;
import edu.rajasekharuni.gearwood.enums.Difficulty;
import edu.rajasekharuni.gearwood.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String keyword, @RequestParam(required = false) Category category, @RequestParam(required = false) Difficulty difficulty, @RequestParam(required = false) BigDecimal minPrice, @RequestParam(required = false) BigDecimal maxPrice, Authentication authentication, Model model) {

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<Product> products = isAdmin ? productService.searchAllProducts(keyword, category, difficulty, minPrice, maxPrice) : productService.searchProducts(keyword, category, difficulty, minPrice, maxPrice);

        model.addAttribute("products", products);
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("categories", Category.values());
        model.addAttribute("difficulties", Difficulty.values());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "products";
    }

    @GetMapping("/products/{productCode}")
    public String productDetails(@PathVariable String productCode, Model model, Authentication authentication) {

        Product product = productService.getProductByCode(productCode);

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!product.isActive() && !isAdmin) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("imageNumbers", getProductImageNumbers(productCode));

        return "product-details";
    }

    private List<Integer> getProductImageNumbers(String productCode) {

        List<Integer> imageNumbers = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String imageNumber = String.format("%02d", i);

            Path imagePath = Path.of("src/main/resources/static/images/product-images", productCode + "-" + imageNumber + ".jpg");

            if (Files.exists(imagePath)) {
                imageNumbers.add(i);
            }
        }

        return imageNumbers;
    }
}