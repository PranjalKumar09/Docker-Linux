package com.ecom.service;

import com.ecom.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
     Category saveCategory(Category category);

     Boolean existCategory(String categoryName);

     List<Category> getAllCategory();
     Page<Category> getAllCategory(Pageable pageable);

     Boolean deleteCategory(int id);

     Category getCategoryById(int id);

     List<Category> getAllActiveCategory();


}
