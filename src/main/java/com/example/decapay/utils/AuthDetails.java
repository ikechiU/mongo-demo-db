package com.example.decapay.utils;

import com.example.decapay.entities.User;
import com.example.decapay.exception.AuthenticationException;
import com.example.decapay.exception.ValidationException;
import com.example.decapay.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AuthDetails {
    @Value(value = "${auth.user}")
    private String AUTH_USER;
    @Value(value = "${active.auth.user}")
    private String ACTIVE_AUTH_USER;

    private final UserRepository userRepository;
    private final LocalMemStorage localStorage;

    public User getAuthorizedUser(Principal principal) {
        if (principal != null) {
            final UserDetails currentUser = (UserDetails) ((Authentication) principal).getPrincipal();
            return userRepository.findByEmailAddress(currentUser.getUsername())
                    .orElseThrow(()-> new AuthenticationException("Kindly, login to access your dashboard."));
        } else{
            throw new AuthenticationException("Kindly, login to access your dashboard.");
        }
    }

    public User validateActiveUser(Principal principal) {
        User user = getAuthorizedUser(principal);

        String activeTokenKey = ACTIVE_AUTH_USER + user.getEmailAddress();
        String tokenKey = AUTH_USER + user.getEmailAddress();

        String activeToken = localStorage.getValueByKey(activeTokenKey);
        String token = localStorage.getValueByKey(tokenKey);

        if (activeToken == null || !activeToken.equals(token))
            throw new ValidationException("Token expired. Kindly, login again.");

        return user;
    }
}
