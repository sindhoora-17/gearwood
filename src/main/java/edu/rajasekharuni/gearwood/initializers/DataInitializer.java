package edu.rajasekharuni.gearwood.initializers;

import edu.rajasekharuni.gearwood.entities.Manufacturer;
import edu.rajasekharuni.gearwood.entities.Product;
import edu.rajasekharuni.gearwood.entities.Role;
import edu.rajasekharuni.gearwood.entities.User;
import edu.rajasekharuni.gearwood.enums.Category;
import edu.rajasekharuni.gearwood.enums.Difficulty;
import edu.rajasekharuni.gearwood.repositories.ManufacturerRepository;
import edu.rajasekharuni.gearwood.repositories.ProductRepository;
import edu.rajasekharuni.gearwood.repositories.RoleRepository;
import edu.rajasekharuni.gearwood.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DataInitializer {

    private final ManufacturerRepository manufacturerRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void loadData() {
        loadRoles();
        loadUsers();
        loadManufacturers();
        loadProducts();
    }

    private void loadRoles() {

        if (roleRepository.count() > 0) {
            return;
        }

        Role customerRole = Role.builder().name("ROLE_CUSTOMER").build();

        Role adminRole = Role.builder().name("ROLE_ADMIN").build();

        roleRepository.save(customerRole);
        roleRepository.save(adminRole);
    }

    private void loadUsers() {

        if (userRepository.count() > 0) {
            return;
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER").orElseThrow();

        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();

        User customer = User.builder().firstName("John").lastName("Customer").email("customer@gmail.com").password(passwordEncoder.encode("password")).role(customerRole).build();

        User admin = User.builder().firstName("Admin").lastName("User").email("admin.user@gmail.com").password(passwordEncoder.encode("password")).role(adminRole).build();

        userRepository.save(customer);
        userRepository.save(admin);
    }

    private void loadManufacturers() {
        if (manufacturerRepository.count() > 0) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/wooden_puzzle_manufacturers.csv")))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);

                Manufacturer manufacturer = Manufacturer.builder().name(parts[0].trim()).address(parts[1].trim()).contactPhone(parts[2].trim()).contactEmail(parts[3].trim()).build();

                manufacturerRepository.save(manufacturer);
            }

        } catch (Exception e) {
            System.out.println("Error loading manufacturers: " + e.getMessage());
        }
    }

    private void loadProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/wooden_puzzle_products.csv")))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                String productCode = parts[0].trim();
                String name = parts[1].trim();
                String manufacturerName = parts[2].trim();
                Integer pieces = Integer.parseInt(parts[3].trim());
                Difficulty difficulty = Difficulty.valueOf(parts[4].trim().toUpperCase());
                BigDecimal price = new BigDecimal(parts[5].replace("$", "").trim());
                BigDecimal wholesaleCost = new BigDecimal(parts[6].replace("$", "").trim());
                Category category = Category.valueOf(parts[7].trim().toUpperCase().replace("-", "_").replace(" ", "_"));
                String shortDescription = parts[8].trim().replace("\"", "");
                String longDescription = parts[9].trim().replace("\"", "");
                Manufacturer manufacturer = manufacturerRepository.findByName(manufacturerName).orElseThrow(() -> new RuntimeException("Manufacturer not found: " + manufacturerName));
                Product product = Product.builder().productCode(productCode).name(name).manufacturer(manufacturer).numberOfPieces(pieces).difficulty(difficulty).category(category).price(price).wholesaleCost(wholesaleCost).shortDescription(shortDescription).longDescription(longDescription).active(true).acquiredDate(LocalDate.now()).build();
                productRepository.save(product);
            }

        } catch (Exception e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}