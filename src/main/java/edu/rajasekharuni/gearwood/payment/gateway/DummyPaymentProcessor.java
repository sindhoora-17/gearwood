package edu.rajasekharuni.gearwood.payment.gateway;

import edu.rajasekharuni.gearwood.payment.model.PaymentRequest;
import edu.rajasekharuni.gearwood.payment.model.PaymentResult;
import edu.rajasekharuni.gearwood.payment.model.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Service
public class DummyPaymentProcessor implements PaymentProcessor {

    private static final Set<String> DECLINED_CARDS = Set.of("4000000000000002", "4111111111111112", "4222222222222220");

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        validateRequest(request);

        String normalizedCardNumber = normalizeCardNumber(request.getCardNumber());

        if (DECLINED_CARDS.contains(normalizedCardNumber)) {
            return new PaymentResult(PaymentStatus.DECLINED, "The payment was declined by the payment processor.", null);
        }

        return new PaymentResult(PaymentStatus.APPROVED, "Payment approved.", UUID.randomUUID().toString());
    }

    private void validateRequest(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null");
        }

        validateRequiredText(request.getCardholderName(), "Cardholder name");
        validateRequiredText(request.getCardNumber(), "Card number");
        validateRequiredText(request.getExpirationMonth(), "Expiration month");
        validateRequiredText(request.getExpirationYear(), "Expiration year");
        validateRequiredText(request.getCvv(), "CVV");

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }

        String normalizedCardNumber = normalizeCardNumber(request.getCardNumber());
        if (!normalizedCardNumber.matches("\\d{13,19}")) {
            throw new IllegalArgumentException("Card number must contain 13 to 19 digits");
        }

        if (!request.getCvv().matches("\\d{3,4}")) {
            throw new IllegalArgumentException("CVV must be 3 or 4 digits");
        }

        if (!request.getExpirationMonth().matches("\\d{2}")) {
            throw new IllegalArgumentException("Expiration month must be 2 digits");
        }

        if (!request.getExpirationYear().matches("\\d{4}")) {
            throw new IllegalArgumentException("Expiration year must be 4 digits");
        }
    }

    private void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
    }

    private String normalizeCardNumber(String cardNumber) {
        return cardNumber.replaceAll("[^\\d]", "");
    }
}
