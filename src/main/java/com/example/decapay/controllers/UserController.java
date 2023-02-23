package com.example.decapay.controllers;


import com.example.decapay.pojos.ActivationRequest;
import com.example.decapay.pojos.ApiResponse;
import com.example.decapay.pojos.AuthRequest;
import com.example.decapay.pojos.SignupRequest;
import com.example.decapay.services.UserService;
import com.example.decapay.utils.ResponseProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {

    private  final UserService userService;
    private  final ResponseProvider responseProvider;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> createUser(@RequestBody SignupRequest request){
        return  responseProvider.success(userService.createUser(request));
    }
    @PostMapping("/auth")
    public ResponseEntity<ApiResponse<Object>>  authenticateUser(@RequestBody AuthRequest request){
        return  responseProvider.success(userService.signIn(request));
    }

    @PostMapping("/hello")
    public ResponseEntity<ApiResponse<Object>>  sayHello(Principal principal){
        return  responseProvider.success(userService.hello(principal));
    }

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<Object>>  activateUser(@RequestBody ActivationRequest request){
        return  responseProvider.success(userService.activateUser(request));
    }

    @PostMapping("/resend-token")
    public ResponseEntity<ApiResponse<Object>> resendUserToken(@RequestParam("email") String email){
        return  responseProvider.success(userService.resendToken(email));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(Principal principal) {
        return  responseProvider.success(userService.logout(principal));
    }
}
