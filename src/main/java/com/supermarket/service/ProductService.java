package com.supermarket.service;

import com.supermarket.common.BusinessException;
import com.supermarket.dto.ProductRequest;
import com.supermarket.dto.ProductResponse;
import com.supermarket.entity.Product;
import com.supermarket.entity.ProductCategory;
import com.supermarket.entity.ProductStatus;
import com.supermarket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSpecification(request.getSpecification());
        product.setCategory(request.getCategory());
        product.setStatus(request.getStatus());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }
    
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "商品不存在"));
        return toResponse(product);
    }
    
    public Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable).map(this::toResponse);
    }
    
    public Page<ProductResponse> getProductsByStatus(ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable).map(this::toResponse);
    }
    
    public Page<ProductResponse> searchProducts(String name, Pageable pageable) {
        return productRepository.findByNameContaining(name, pageable).map(this::toResponse);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "商品不存在"));
        
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSpecification(request.getSpecification());
        product.setCategory(request.getCategory());
        product.setStatus(request.getStatus());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        
        Product updated = productRepository.save(product);
        return toResponse(updated);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException(404, "商品不存在");
        }
        productRepository.deleteById(id);
    }
    
    @Transactional
    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "商品不存在"));
        
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new BusinessException(400, "库存不足");
        }
        product.setStock(newStock);
        
        if (newStock == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        }
        
        Product updated = productRepository.save(product);
        return toResponse(updated);
    }
    
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getSpecification(),
                product.getCategory(),
                product.getStatus(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
