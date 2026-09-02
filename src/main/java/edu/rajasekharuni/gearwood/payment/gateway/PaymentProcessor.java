package edu.rajasekharuni.gearwood.payment.gateway;

import edu.rajasekharuni.gearwood.payment.model.PaymentRequest;
import edu.rajasekharuni.gearwood.payment.model.PaymentResult;

public interface PaymentProcessor {
    PaymentResult processPayment(PaymentRequest request);
}
