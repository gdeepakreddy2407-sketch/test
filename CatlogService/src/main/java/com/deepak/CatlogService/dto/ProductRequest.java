package com.deepak.CatlogService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    public String name;

    public String description;

    public BigDecimal price;

    public String category;
}
