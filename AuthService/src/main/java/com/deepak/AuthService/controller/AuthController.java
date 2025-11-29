package com.deepak.AuthService.controller;

import com.deepak.AuthService.dto.AuthResponseDto;
import com.deepak.AuthService.dto.IntrospectResponseDto;
import com.deepak.AuthService.dto.LoginRequestDto;
import com.deepak.AuthService.dto.RefreshRequestDto;
import com.deepak.AuthService.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {
    @PostMapping("/api/auth/login")
    @Autowired
    public AuthResponseDto logIn(@RequestBody @Valid LoginRequestDto loginRequestDto){
        AuthResponseDto authenticate = new AuthResponseDto();
        return authenticate;
    }
    @PostMapping("/api/auth/refresh")
    @Autowired
    public AuthResponseDto refresh(@RequestBody @Valid RefreshRequestDto refreshRequestDto){
        AuthResponseDto authenticate = new AuthResponseDto();
        return authenticate;
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> getJwks() {
        Map<String, Object> jwks = JwtService.getJwks();
        return ResponseEntity.ok(jwks);
    }
}
