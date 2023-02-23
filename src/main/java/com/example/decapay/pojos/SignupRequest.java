package com.example.decapay.pojos;

import lombok.Data;

/**
 * @author Ikechi Ucheagwu
 * @created 08/12/2022 - 00:04
 * @project Decapay
 */


@Data
public class SignupRequest {
    private String name;
    private String emailAddress;
    private String password;
}