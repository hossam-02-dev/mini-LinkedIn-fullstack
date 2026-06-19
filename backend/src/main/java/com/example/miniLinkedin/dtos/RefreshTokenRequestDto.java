package com.example.miniLinkedin.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshTokenRequestDto {
    @NotBlank(message = "Le refresh token est obligatoire")
    private String refreshToken;
}