package edu.rajasekharuni.gearwood.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResult {

    private PaymentStatus status;
    private String message;
    private String transactionId;
}