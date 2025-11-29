package com.deepak.AuthService.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    String accessToken;
    String tokentype;
    long expiresIn;

}
