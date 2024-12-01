package com.ecom.service.impl;

import com.ecom.entity.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;


    @Override
    public Page<Product> getAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product saveProduct(Product product) {
        return  productRepository.save(product);
    }

    @Override
    public Boolean deleteProduct(Integer id) {
        Product product = productRepository.getReferenceById(id);
        if (product != null) {
            productRepository.delete(product);
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public Product getProductById(Integer id) {
        try {
            return productRepository.getReferenceById(id);
        } catch (EntityNotFoundException ex) {
            return null;
        }
    }

    @Override
    public Product updateProduct(Product product, MultipartFile file) {
        Product dbProduct = productRepository.getReferenceById(product.getId());

        String imageName = file.isEmpty() ? dbProduct.getImage() : file.getOriginalFilename();
        System.out.println("updateProduct -> " + imageName);
        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setStock(product.getStock());
        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setDiscount(product.getDiscount());
        Double discount_price = product.getPrice() - ((product.getDiscount()/100.0) * product.getPrice());
        dbProduct.setDiscount_price(discount_price);



        dbProduct.setImage(imageName);
        Product savedProduct = productRepository.save(dbProduct);

        if (!file.isEmpty()) {
            try {
                Path uploadPath = Paths.get("src/main/resources/static/img/product_img/");

                Files.copy(file.getInputStream(), uploadPath.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return savedProduct;


    }

    @Override
    public List<Product> getAllActiveProduct() {
        return productRepository.findByIsActiveTrue();
    }

    @Override
    public List<Product> getAllProductByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> searchProduct(String string) {
        return productRepository.searchByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(string, string, string);
    }

    @Override
    public Page<Product> searchProduct(String string, Integer pageNo, Integer pageSize) {


        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return  productRepository.searchByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(string, string, string, pageable);
    }

    @Override
    public Page<Product> getAllActiveProductPagination(Integer page, Integer size, String category) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productList;
        if (Objects.equals(category, "all")) {
            productList = productRepository.findByIsActiveTrue(pageable);
        } else {
            productList = productRepository.findByCategory(category,pageable);
        }

        return productList;
    }
}
