package edu.rajasekharuni.gearwood.entities;

import edu.rajasekharuni.gearwood.enums.Category;
import edu.rajasekharuni.gearwood.enums.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String productCode;

    @NotBlank
    private String name;

    @ManyToOne
    @NotNull
    private Manufacturer manufacturer;

    @NotNull
    @Min(1)
    private Integer numberOfPieces;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal price;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal wholesaleCost;

    @NotBlank
    @Column(length = 500)
    private String shortDescription;

    @NotBlank
    @Column(length = 3000)
    private String longDescription;

    private boolean active = true;

    @NotNull
    private LocalDate acquiredDate;
}