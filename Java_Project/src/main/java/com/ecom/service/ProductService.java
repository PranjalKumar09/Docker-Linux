package com.ecom.service;

import com.ecom.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product saveProduct(Product product);
    Page<Product> getAllProduct(Pageable pageable);
    Boolean deleteProduct(Integer id);
    Product getProductById(Integer id);
    Product updateProduct(Product product, MultipartFile file);

    List<Product> getAllActiveProduct();
    List<Product> getAllProductByCategory(String category);


    List<Product> searchProduct(String string);
    Page<Product> searchProduct(String string, Integer pageNo,Integer pageSize);

    Page<Product> getAllActiveProductPagination(Integer page, Integer size, String category);

}
