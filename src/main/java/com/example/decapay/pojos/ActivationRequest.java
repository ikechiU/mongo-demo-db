package com.example.decapay.pojos;
import lombok.Data;

@Data
public class ActivationRequest {
    private String emailAddress;
    private String token;
}
