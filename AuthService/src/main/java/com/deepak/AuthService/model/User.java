package com.deepak.AuthService.model;

import jakarta.validation.constraints.Email;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
public class User {
    @id
    String id;
    @Indexed(unique=true)
    String username;
    @Email
    String email;
    String passwordHash;
    List<String> roles;
    Boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
