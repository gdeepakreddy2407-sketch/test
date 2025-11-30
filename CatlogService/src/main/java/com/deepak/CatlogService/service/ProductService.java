package com.deepak.CatlogService.service;

import com.deepak.CatlogService.Entity;
import com.deepak.CatlogService.dto.ProductRequest;
import com.deepak.CatlogService.repository.CatlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Component
public class ProductService {
    @Autowired
    CatlogRepository repo;

    public String addProduct(ProductRequest productRequest){
        Entity entity = new Entity();
        entity.setName(productRequest.getName());
        entity.setCategory(productRequest.getCategory());
        entity.setPrice(productRequest.getPrice());
        entity.setDescription(productRequest.getDescription());
        repo.save(entity);
        return "Product saved successfully with ID: " + entity.getId();

    }
}
