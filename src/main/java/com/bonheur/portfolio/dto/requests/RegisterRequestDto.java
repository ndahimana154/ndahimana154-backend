package com.bonheur.portfolio.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class RegisterRequestDto {
    @NotBlank(message = "Email is required ")
    private String email;

    @NotBlank(message = "Names is required")
    private String names;

    @NotBlank(message = "Password is required")
    private String password;
}
