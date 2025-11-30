package com.deepak.CatlogService.repository;

import com.deepak.CatlogService.Entity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CatlogRepository extends MongoRepository<Entity, String> {

    List<Entity> findByCategory(String category);
    Optional<Entity> findById(String id);

    List<Entity> findByName(String name);
}
