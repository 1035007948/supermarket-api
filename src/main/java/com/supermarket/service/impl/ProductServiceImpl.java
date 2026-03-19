package com.supermarket.service.impl;

import com.supermarket.entity.Product;
import com.supermarket.enums.ProductCategory;
import com.supermarket.enums.ProductStatus;
import com.supermarket.repository.ProductRepository;
import com.supermarket.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("商品不存在，ID: " + id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getProductsPage(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setSpecification(product.getSpecification());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStatus(product.getStatus());
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("商品不存在，ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Override
    @Transactional
    public Product updateProductStatus(Long id, ProductStatus status) {
        Product product = getProductById(id);
        product.setStatus(status);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByCategoryAndStatus(ProductCategory category, ProductStatus status) {
        return productRepository.findByCategoryAndStatus(category, status);
    }
}