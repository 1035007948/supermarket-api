package com.supermarket.controller;

import com.supermarket.common.ApiResponse;
import com.supermarket.dto.ProductRequest;
import com.supermarket.dto.ProductResponse;
import com.supermarket.entity.ProductCategory;
import com.supermarket.entity.ProductStatus;
import com.supermarket.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ApiResponse.success("商品创建成功", response);
    }
    
    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/category/{category}")
    public ApiResponse<Page<ProductResponse>> getProductsByCategory(
            @PathVariable ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.getProductsByCategory(category, pageable);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/status/{status}")
    public ApiResponse<Page<ProductResponse>> getProductsByStatus(
            @PathVariable ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.getProductsByStatus(status, pageable);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/search")
    public ApiResponse<Page<ProductResponse>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.searchProducts(name, pageable);
        return ApiResponse.success(products);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ApiResponse.success("商品更新成功", response);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("商品删除成功", null);
    }
    
    @PatchMapping("/{id}/stock")
    public ApiResponse<ProductResponse> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        ProductResponse response = productService.updateStock(id, quantity);
        return ApiResponse.success("库存更新成功", response);
    }
}
