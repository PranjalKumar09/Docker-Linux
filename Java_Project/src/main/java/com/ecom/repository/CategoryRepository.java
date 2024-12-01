package com.ecom.repository;

import com.ecom.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Boolean existsByTitle(String title);
    List<Category> findByIsActiveTrue();
}
