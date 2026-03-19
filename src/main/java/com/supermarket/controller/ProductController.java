package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.entity.Product;
import com.supermarket.enums.ProductCategory;
import com.supermarket.enums.ProductStatus;
import com.supermarket.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public Result<Product> createProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return Result.success(createdProduct);
    }

    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    @GetMapping
    public Result<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return Result.success(products);
    }

    @GetMapping("/page")
    public Result<Page<Product>> getProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productService.getProductsPage(pageable);
        return Result.success(productPage);
    }

    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return Result.success(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @GetMapping("/category/{category}")
    public Result<List<Product>> getProductsByCategory(@PathVariable ProductCategory category) {
        List<Product> products = productService.getProductsByCategory(category);
        return Result.success(products);
    }

    @GetMapping("/status/{status}")
    public Result<List<Product>> getProductsByStatus(@PathVariable ProductStatus status) {
        List<Product> products = productService.getProductsByStatus(status);
        return Result.success(products);
    }

    @GetMapping("/search")
    public Result<List<Product>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return Result.success(products);
    }

    @PutMapping("/{id}/status")
    public Result<Product> updateProductStatus(@PathVariable Long id, @RequestParam ProductStatus status) {
        Product updatedProduct = productService.updateProductStatus(id, status);
        return Result.success(updatedProduct);
    }

    @GetMapping("/category/{category}/status/{status}")
    public Result<List<Product>> getProductsByCategoryAndStatus(
            @PathVariable ProductCategory category,
            @PathVariable ProductStatus status) {
        List<Product> products = productService.getProductsByCategoryAndStatus(category, status);
        return Result.success(products);
    }
}