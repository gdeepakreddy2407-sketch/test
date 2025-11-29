package com.deepak.AuthService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequestDto {
    @NotBlank
    String refreshToken;
}
