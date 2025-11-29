package com.deepak.AuthService.dto;

import java.util.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectResponseDto {
    boolean active;
    String Username;
    List<String> roles;
    Map<String,Object> claims;
    long exp;
}
