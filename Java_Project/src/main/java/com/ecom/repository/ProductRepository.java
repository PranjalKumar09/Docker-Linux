package com.ecom.repository;

import com.ecom.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByIsActiveTrue();
    List<Product> findByCategory(String category);
    List<Product> searchByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String description, String category);
    Page<Product> searchByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String description, String category, Pageable pageable);
    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product>  findByCategory(String category, Pageable pageable);

}
