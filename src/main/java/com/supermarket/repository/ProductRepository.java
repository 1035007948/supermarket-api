package com.supermarket.repository;

import com.supermarket.entity.Product;
import com.supermarket.enums.ProductCategory;
import com.supermarket.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByNameContaining(String name);

    List<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status);
}