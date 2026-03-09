package com.bonheur.portfolio.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "Email is required ")
    private String email;

    @NotBlank(message = "Names is required")
    private String names;

    @NotBlank(message = "Password is required")
    private String password;
}
