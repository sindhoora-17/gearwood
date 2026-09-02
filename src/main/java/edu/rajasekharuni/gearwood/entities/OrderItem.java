package edu.rajasekharuni.gearwood.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    private String productNameAtPurchase;

    private BigDecimal unitPriceAtPurchase;

    private Integer quantity;

    private BigDecimal lineTotal;
}