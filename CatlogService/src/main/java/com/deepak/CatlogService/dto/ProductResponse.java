package com.deepak.CatlogService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    public String id;

    public String name;

    public String description;

    public BigDecimal price;

    public String category;
}
