package com.ecom.service.impl;

import com.ecom.entity.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> getAllCategory(Pageable pageable) {
        return  categoryRepository.findAll(pageable);
    }

    @Override
    public Boolean deleteCategory(int id) {
        Category category = categoryRepository.findById(id).get();
        if (category != null) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Boolean existCategory(String categoryName) {
        return categoryRepository.existsByTitle(categoryName);
    }

    @Override
    public Category saveCategory(Category category){
        return categoryRepository.save(category);
    }


    @Override
    public Category getCategoryById(int id) {
//        Category category =  ;
        return categoryRepository.findById(id).get();
    }

    @Override
    public List<Category> getAllActiveCategory() {
        return categoryRepository.findByIsActiveTrue();
    }
}
