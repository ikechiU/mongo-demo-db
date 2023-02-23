package com.example.decapay.services;

import com.example.decapay.entities.User;
import com.example.decapay.enums.Status;
import com.example.decapay.exception.AuthenticationException;
import com.example.decapay.exception.ValidationException;
import com.example.decapay.pojos.ActivationRequest;
import com.example.decapay.pojos.AuthRequest;
import com.example.decapay.pojos.AuthResponse;
import com.example.decapay.pojos.SignupRequest;
import com.example.decapay.repositories.UserRepository;
import com.example.decapay.security.JwtUtil;
import com.example.decapay.utils.AppUtil;
import com.example.decapay.utils.AuthDetails;
import com.example.decapay.utils.LocalMemStorage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;

@Service
@AllArgsConstructor
public class UserService {
    @Value(value = "${user.email.activate}")
    private String USER_EMAIL_ACTIVATE;
    @Value(value = "${auth.user}")
    private String AUTH_USER;
    @Value(value = "${active.auth.user}")
    private String ACTIVE_AUTH_USER;
    private final AppUtil app;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager auth;

    private final JwtUtil jwtUtil;

    private final LocalMemStorage localStorage;

    private final AuthDetails authDetails;

    public User createUser(SignupRequest request) {

        if (!app.validEmail(request.getEmailAddress()))
            throw new ValidationException("Invalid email address");

        boolean userExist = userRepository.existsByEmailAddress(request.getEmailAddress());
        if (userExist)
            throw new ValidationException("User already exist");

        User newUser = app.getMapper().convertValue(request, User.class);
        newUser.setUuid(app.generateSerialNumber("usr"));
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(Status.INACTIVE);

        //generate a activation or OTP for the user
        String token = app.generateSerialNumber("v");
        localStorage.save(USER_EMAIL_ACTIVATE + request.getEmailAddress(), token, 0);

        //nest thing is to send the OTP or the token to the user email or Phone
        app.print("Token:");
        app.print(token);
        //end of email

        return userRepository.save(newUser);
    }

    public User activateUser(ActivationRequest request) {
        if (!app.validEmail(request.getEmailAddress()))
            throw new ValidationException("Invalid email address");

        User existingUser = userRepository.findByEmailAddress(request.getEmailAddress()).orElse(null);
        if (existingUser == null)
            throw new ValidationException("User not found");


        String systemToken = localStorage.getValueByKey(request.getEmailAddress());
        if (systemToken == null)
            throw new ValidationException("Token expired");

        if (!systemToken.equalsIgnoreCase(request.getToken()))
            throw new ValidationException("Invalid token");

        existingUser.setStatus(Status.ACTIVE);
        existingUser.setUpdatedDate(new Date());

        return userRepository.save(existingUser);
    }

    public String resendToken(String email) {
        if (!app.validEmail(email))
            throw new ValidationException("Invalid email address");

        User existingUser = userRepository.findByEmailAddress(email).orElse(null);
        if (existingUser == null)
            throw new ValidationException("User not found");

        //generate a activation or OTP for the user
        String token = app.generateSerialNumber("v");
        localStorage.save(USER_EMAIL_ACTIVATE + email, token, 0);

        //nest thing is to send the OTP or the token to the user email or Phone
        app.print("Token:");
        app.print(token);
        //end of email

        return "Token has been sent successfully";
    }


    public AuthResponse signIn(AuthRequest loginRequest) {
        try {
            Authentication authentication = auth.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmailAddress(), loginRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                User user = userRepository.findByEmailAddress(loginRequest.getEmailAddress()).orElse(null);
                if (user != null) {
                    user.setLastLoginDate(new Date());
                    userRepository.save(user);

                    app.print(loginRequest);

                    String accessToken = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                            loginRequest.getEmailAddress(), loginRequest.getPassword(), new ArrayList<>()));

                    String activeTokenKey = ACTIVE_AUTH_USER + loginRequest.getEmailAddress();
                    localStorage.saveToken(activeTokenKey, accessToken);

                    AuthResponse loginResponse = new AuthResponse();
                    user.setPassword("*****************");
                    loginResponse.setUser(user);
                    loginResponse.setToken(accessToken);
                    return loginResponse;
                } else {
                    throw new AuthenticationException("Invalid Login Credentials");
                }
            } else {
                throw new AuthenticationException("Invalid Username or Password");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Invalid Username or Password");
        }
    }

    public String logout(Principal principal) {
        User user = authDetails.validateActiveUser(principal);

        String activeTokenKey = ACTIVE_AUTH_USER + user.getEmailAddress();
        String tokenKey = AUTH_USER + user.getEmailAddress();

        localStorage.clear(activeTokenKey);
        localStorage.clear(tokenKey);

        return "Logout successful";
    }

    public String hello(Principal principal) {
        authDetails.validateActiveUser(principal);
        return "You are welcome.";
    }

}
