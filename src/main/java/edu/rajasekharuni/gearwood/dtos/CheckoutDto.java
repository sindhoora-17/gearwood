package edu.rajasekharuni.gearwood.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutDto {

    @NotBlank(message = "Full name is required")
    private String shippingName;

    @NotBlank(message = "Address line 1 is required")
    private String streetAddress;

    private String address2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "ZIP is required")
    private String zip;

    @NotBlank(message = "Cardholder name is required")
    private String cardholderName;

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Expiration month is required")
    private String expirationMonth;

    @NotBlank(message = "Expiration year is required")
    private String expirationYear;

    @NotBlank(message = "CVV is required")
    private String cvv;
}