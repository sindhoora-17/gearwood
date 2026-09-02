package edu.rajasekharuni.gearwood.services;

import edu.rajasekharuni.gearwood.entities.Manufacturer;
import edu.rajasekharuni.gearwood.entities.Product;
import edu.rajasekharuni.gearwood.enums.Category;
import edu.rajasekharuni.gearwood.enums.Difficulty;
import edu.rajasekharuni.gearwood.repositories.CartItemRepository;
import edu.rajasekharuni.gearwood.repositories.ManufacturerRepository;
import edu.rajasekharuni.gearwood.repositories.OrderItemRepository;
import edu.rajasekharuni.gearwood.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductService {

    private final ProductRepository productRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrueOrderByNameAsc();
    }

    public Product getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> searchProducts(String keyword, Category category, Difficulty difficulty, BigDecimal minPrice, BigDecimal maxPrice) {

        Stream<Product> productStream = productRepository.findByActiveTrueOrderByNameAsc().stream();

        if (keyword != null && !keyword.isBlank()) {
            productStream = productStream.filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()));
        }

        if (category != null) {
            productStream = productStream.filter(product -> product.getCategory() == category);
        }

        if (difficulty != null) {
            productStream = productStream.filter(product -> product.getDifficulty() == difficulty);
        }

        if (minPrice != null) {
            productStream = productStream.filter(product -> product.getPrice().compareTo(minPrice) >= 0);
        }

        if (maxPrice != null) {
            productStream = productStream.filter(product -> product.getPrice().compareTo(maxPrice) <= 0);
        }

        return productStream.toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void createProduct(Product product, Long manufacturerId) {

        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId).orElseThrow();

        product.setManufacturer(manufacturer);
        product.setActive(true);

        if (product.getAcquiredDate() == null) {
            product.setAcquiredDate(LocalDate.now());
        }

        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Product getAnyProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateProduct(String productCode, Product updatedProduct, Long manufacturerId) {

        Product existingProduct = getAnyProductByCode(productCode);

        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId).orElseThrow();

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setManufacturer(manufacturer);
        existingProduct.setNumberOfPieces(updatedProduct.getNumberOfPieces());
        existingProduct.setDifficulty(updatedProduct.getDifficulty());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setWholesaleCost(updatedProduct.getWholesaleCost());

        if (updatedProduct.getAcquiredDate() != null) {
            existingProduct.setAcquiredDate(updatedProduct.getAcquiredDate());
        }

        existingProduct.setShortDescription(updatedProduct.getShortDescription());
        existingProduct.setLongDescription(updatedProduct.getLongDescription());

        productRepository.save(existingProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void toggleProductStatus(String productCode) {

        Product product = getAnyProductByCode(productCode);

        product.setActive(!product.isActive());

        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(String productCode) {

        Product product = getAnyProductByCode(productCode);

        boolean existsInCart = cartItemRepository.existsByProduct(product);
        boolean existsInOrders = orderItemRepository.existsByProduct(product);

        if (existsInCart || existsInOrders) {
            product.setActive(false);
            productRepository.save(product);
            return;
        }

        deleteProductImages(productCode);

        productRepository.delete(product);
    }

    private void deleteProductImages(String productCode) {

        String imageFolder = "src/main/resources/static/images/product-images";

        for (int i = 1; i <= 10; i++) {
            String imageNumber = String.format("%02d", i);
            Path imagePath = Path.of(imageFolder, productCode + "-" + imageNumber + ".jpg");

            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.out.println("Could not delete image: " + imagePath);
            }
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Product> searchAllProducts(String keyword, Category category, Difficulty difficulty, BigDecimal minPrice, BigDecimal maxPrice) {

        Stream<Product> productStream = productRepository.findAllByOrderByNameAsc().stream();

        if (keyword != null && !keyword.isBlank()) {
            productStream = productStream.filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()));
        }

        if (category != null) {
            productStream = productStream.filter(product -> product.getCategory() == category);
        }

        if (difficulty != null) {
            productStream = productStream.filter(product -> product.getDifficulty() == difficulty);
        }

        if (minPrice != null) {
            productStream = productStream.filter(product -> product.getPrice().compareTo(minPrice) >= 0);
        }

        if (maxPrice != null) {
            productStream = productStream.filter(product -> product.getPrice().compareTo(maxPrice) <= 0);
        }

        return productStream.toList();
    }
}