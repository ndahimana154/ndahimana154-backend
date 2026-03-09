package com.bonheur.portfolio.controllers.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonheur.portfolio.dto.requests.LoginRequestDto;
import com.bonheur.portfolio.dto.requests.RegisterRequestDto;
import com.bonheur.portfolio.services.api.AuthServices;
import com.bonheur.portfolio.utils.ValidationUtils;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/backoffice/auth")
public class AuthController {

    private final AuthServices authService;
    private final ValidationUtils validationUtils;

    public AuthController(AuthServices authService, ValidationUtils validationUtils) {
        this.authService = authService;
        this.validationUtils = validationUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequestDto dto) {
        ResponseEntity<Map<String, Object>> validationResponse = validationUtils.validateAndBuildResponse(dto);

        if (validationResponse != null) {
            return validationResponse;
        }

        Map<String, Object> result = authService.registerUser(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDto dto) {
        ResponseEntity<Map<String, Object>> validationResponse = validationUtils.validateAndBuildResponse(dto);

        if (validationResponse != null) {
            return validationResponse;
        }
        Map<String, Object> result = authService.loginUser(dto);
        return ResponseEntity.ok(result);
    }

}
