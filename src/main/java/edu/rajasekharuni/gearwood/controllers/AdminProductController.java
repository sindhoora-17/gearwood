package edu.rajasekharuni.gearwood.controllers;

import edu.rajasekharuni.gearwood.entities.Product;
import edu.rajasekharuni.gearwood.enums.Category;
import edu.rajasekharuni.gearwood.enums.Difficulty;
import edu.rajasekharuni.gearwood.repositories.ManufacturerRepository;
import edu.rajasekharuni.gearwood.repositories.ProductRepository;
import edu.rajasekharuni.gearwood.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ManufacturerRepository manufacturerRepository;

    @GetMapping("/admin/products")
    public String adminProducts(Model model) {

        model.addAttribute("products", productRepository.findAllByOrderByNameAsc());

        return "admin-products";
    }

    @GetMapping("/admin/products/create")
    public String createProductPage(Model model) {

        model.addAttribute("product", new Product());
        model.addAttribute("manufacturers", manufacturerRepository.findAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("difficulties", Difficulty.values());

        return "create-product";
    }

    @PostMapping("/admin/products/create")
    public String createProduct(@ModelAttribute Product product, @RequestParam Long manufacturerId) {

        productService.createProduct(product, manufacturerId);

        return "redirect:/products";
    }

    @GetMapping("/admin/products/edit/{productCode}")
    public String editProductPage(@PathVariable String productCode, Model model) {

        model.addAttribute("product", productService.getAnyProductByCode(productCode));
        model.addAttribute("manufacturers", manufacturerRepository.findAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("difficulties", Difficulty.values());

        return "edit-product";
    }

    @PostMapping("/admin/products/edit/{productCode}")
    public String updateProduct(@PathVariable String productCode, @ModelAttribute Product product, @RequestParam Long manufacturerId) {

        productService.updateProduct(productCode, product, manufacturerId);

        return "redirect:/products/" + productCode;
    }

    @PostMapping("/admin/products/toggle/{productCode}")
    public String toggleProductStatus(@PathVariable String productCode) {

        productService.toggleProductStatus(productCode);

        return "redirect:/products";
    }

    @PostMapping("/admin/products/delete/{productCode}")
    public String deleteProduct(@PathVariable String productCode, RedirectAttributes redirectAttributes) {

        productService.deleteProduct(productCode);

        redirectAttributes.addFlashAttribute("success", "Product deleted successfully. If the product was linked to carts or orders, it was deactivated instead.");

        return "redirect:/products";
    }
}