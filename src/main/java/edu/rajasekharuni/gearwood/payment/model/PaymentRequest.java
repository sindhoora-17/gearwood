package edu.rajasekharuni.gearwood.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentRequest {

    private String cardholderName;
    private String cardNumber;
    private String expirationMonth;
    private String expirationYear;
    private String cvv;
    private BigDecimal amount;
}