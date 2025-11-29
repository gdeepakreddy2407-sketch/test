package com.deepak.CatlogService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Document(collection = "products")
public class Entity {
    @Id
    public String id;
    public String name;
    public String description;
    public BigDecimal price;
    public String category;
    @CreatedDate
    public LocalDateTime createdAt;
}
