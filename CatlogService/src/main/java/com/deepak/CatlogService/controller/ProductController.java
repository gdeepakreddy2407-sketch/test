package com.deepak.CatlogService.controller;

import com.deepak.CatlogService.Entity;
import com.deepak.CatlogService.dto.ProductRequest;
import com.deepak.CatlogService.repository.CatlogRepository;
import com.deepak.CatlogService.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    public ProductService productService;
    @Autowired
    CatlogRepository repo;
    @PostMapping("/products")
    public ResponseEntity<String> addProduct(@RequestBody ProductRequest productRequest){
        String message = productService.addProduct(productRequest);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
    @GetMapping("/products/{category}")
    public List<Entity> GetProductsWithCategory(@PathVariable("category") String category){
        List<Entity> files = repo.findByCategory(category);
        return files;
    }
    @GetMapping("/products/id/{id}")
    public Optional<Entity> GetProductsWithId(@PathVariable("id") String id){
        Optional<Entity> files = repo.findById(id);
        return files;
    }
    @GetMapping("/products/name/{name}")
    public List<Entity> GetProductsWithName(@PathVariable("name") String name){
        List<Entity> files = repo.findByName(name);
        return files;
    }

}
