package edu.rajasekharuni.gearwood.payment.gateway;

import edu.rajasekharuni.gearwood.payment.model.PaymentRequest;
import edu.rajasekharuni.gearwood.payment.model.PaymentResult;
import edu.rajasekharuni.gearwood.payment.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DummyPaymentProcessorTests {

    private final DummyPaymentProcessor processor = new DummyPaymentProcessor();

    @Test
    void approvesValidPayment() {
        PaymentResult result = processor.processPayment(validRequest("4111111111111111"));

        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertNotNull(result.getTransactionId());
    }

    @Test
    void declinesConfiguredTestCard() {
        PaymentResult result = processor.processPayment(validRequest("4000000000000002"));

        assertEquals(PaymentStatus.DECLINED, result.getStatus());
        assertEquals(null, result.getTransactionId());
    }

    @Test
    void normalizesFormattedCardNumber() {
        PaymentResult result = processor.processPayment(validRequest("4111 1111 1111 1111"));

        assertEquals(PaymentStatus.APPROVED, result.getStatus());
    }

    @Test
    void rejectsInvalidCardNumber() {
        PaymentRequest request = validRequest("1234");

        assertThrows(IllegalArgumentException.class, () -> processor.processPayment(request));
    }

    @Test
    void rejectsNonPositiveAmount() {
        PaymentRequest request = new PaymentRequest(
                "Test User",
                "4111111111111111",
                "12",
                "2030",
                "123",
                BigDecimal.ZERO
        );

        assertThrows(IllegalArgumentException.class, () -> processor.processPayment(request));
    }

    private PaymentRequest validRequest(String cardNumber) {
        return new PaymentRequest(
                "Test User",
                cardNumber,
                "12",
                "2030",
                "123",
                new BigDecimal("49.99")
        );
    }
}
