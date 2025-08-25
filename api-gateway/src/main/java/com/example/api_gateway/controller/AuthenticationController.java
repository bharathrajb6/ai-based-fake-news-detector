package com.example.api_gateway.controller;

import com.example.api_gateway.model.AuthRequest;
import com.example.api_gateway.model.UserRequest;
import com.example.api_gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;

    private final RestTemplate restTemplate;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        ResponseEntity<Boolean> validationResponse;
        try {
            validationResponse = restTemplate.postForEntity("http://localhost:8081/api/users/validate", authRequest, Boolean.class);
        } catch (HttpClientErrorException exception) {
            String responseBody = exception.getResponseBodyAsString();

            HttpStatusCode statusCode = exception.getStatusCode();

            return ResponseEntity.status(statusCode).contentType(MediaType.APPLICATION_JSON).body(responseBody);
        }
        if (validationResponse.getBody() != null && validationResponse.getBody()) {
            String token = jwtService.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        ResponseEntity<Boolean> validationResponse;
        try {
            validationResponse = restTemplate.postForEntity("http://localhost:8081/api/users/register", userRequest, Boolean.class);
        } catch (HttpClientErrorException exception) {
            // Extract the response body from user-service
            String responseBody = exception.getResponseBodyAsString();

            // Preserve the original status code
            HttpStatusCode statusCode = exception.getStatusCode();

            return ResponseEntity
                    .status(statusCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseBody);
        }

        if (validationResponse.getBody() != null && validationResponse.getBody()) {
            String token = jwtService.generateToken(userRequest.getUsername());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
