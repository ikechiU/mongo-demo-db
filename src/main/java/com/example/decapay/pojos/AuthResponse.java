package com.example.decapay.pojos;

import com.example.decapay.entities.User;
import lombok.Data;

/**
 * @author Ikechi Ucheagwu
 * @created 08/12/2022 - 00:05
 * @project Decapay
 */

@Data
public class AuthResponse {
    private String token;
    private User user;
}
