package com.supermarket.service;

import com.supermarket.entity.Product;
import com.supermarket.enums.ProductCategory;
import com.supermarket.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    Product getProductById(Long id);

    List<Product> getAllProducts();

    Page<Product> getProductsPage(Pageable pageable);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    List<Product> getProductsByCategory(ProductCategory category);

    List<Product> getProductsByStatus(ProductStatus status);

    List<Product> searchProductsByName(String name);

    Product updateProductStatus(Long id, ProductStatus status);

    List<Product> getProductsByCategoryAndStatus(ProductCategory category, ProductStatus status);
}