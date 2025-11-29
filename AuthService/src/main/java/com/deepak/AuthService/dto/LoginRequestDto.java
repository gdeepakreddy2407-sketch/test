package com.deepak.AuthService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank
    String username;
    @NotBlank
    String Password;
}
