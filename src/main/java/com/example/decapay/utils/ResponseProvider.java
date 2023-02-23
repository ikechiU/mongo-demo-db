package com.example.decapay.utils;

import com.example.decapay.pojos.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Ikechi Ucheagwu
 * @created 08/12/2022 - 00:09
 * @project Decapay
 */

@Service
@AllArgsConstructor
public class ResponseProvider {

    public ResponseEntity<ApiResponse<Object>> success(Object payload) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>("Request Successful", true, payload));
    }
}

