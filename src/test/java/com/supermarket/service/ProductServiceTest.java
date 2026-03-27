package com.supermarket.service;

import com.supermarket.entity.Product;
import com.supermarket.enums.ProductCategory;
import com.supermarket.enums.ProductStatus;
import com.supermarket.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setName("测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStock(100);
        testProduct.setSpecification("测试规格");
        testProduct.setCategory(ProductCategory.FOOD);
        testProduct.setStatus(ProductStatus.ON_SALE);
        testProduct = productService.createProduct(testProduct);
    }

    @Test
    void testCreateProduct() {
        assertNotNull(testProduct.getId());
        assertEquals("测试商品", testProduct.getName());
    }

    @Test
    void testGetProductById() {
        Product found = productService.getProductById(testProduct.getId());
        assertNotNull(found);
        assertEquals(testProduct.getName(), found.getName());
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = productService.getAllProducts();
        assertFalse(products.isEmpty());
    }

    @Test
    void testUpdateProduct() {
        Product updateProduct = new Product();
        updateProduct.setName("更新商品");
        updateProduct.setPrice(new BigDecimal("199.99"));
        updateProduct.setStock(200);
        updateProduct.setSpecification("新规格");
        updateProduct.setCategory(ProductCategory.DIGITAL);
        updateProduct.setStatus(ProductStatus.OUT_OF_STOCK);

        Product updated = productService.updateProduct(testProduct.getId(), updateProduct);
        assertEquals("更新商品", updated.getName());
        assertEquals(new BigDecimal("199.99"), updated.getPrice());
        assertEquals(200, updated.getStock());
        assertEquals(ProductCategory.DIGITAL, updated.getCategory());
        assertEquals(ProductStatus.OUT_OF_STOCK, updated.getStatus());
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProduct(testProduct.getId());
        assertThrows(javax.persistence.EntityNotFoundException.class, () -> {
            productService.getProductById(testProduct.getId());
        });
    }

    @Test
    void testGetProductsByCategory() {
        List<Product> products = productService.getProductsByCategory(ProductCategory.FOOD);
        assertFalse(products.isEmpty());
        products.forEach(p -> assertEquals(ProductCategory.FOOD, p.getCategory()));
    }

    @Test
    void testGetProductsByStatus() {
        List<Product> products = productService.getProductsByStatus(ProductStatus.ON_SALE);
        assertFalse(products.isEmpty());
        products.forEach(p -> assertEquals(ProductStatus.ON_SALE, p.getStatus()));
    }

    @Test
    void testSearchProductsByName() {
        List<Product> products = productService.searchProductsByName("测试");
        assertFalse(products.isEmpty());
    }

    @Test
    void testUpdateProductStatus() {
        Product updated = productService.updateProductStatus(testProduct.getId(), ProductStatus.OFF_SHELF);
        assertEquals(ProductStatus.OFF_SHELF, updated.getStatus());
    }

    @Test
    void testGetProductsByCategoryAndStatus() {
        List<Product> products = productService.getProductsByCategoryAndStatus(ProductCategory.FOOD, ProductStatus.ON_SALE);
        assertFalse(products.isEmpty());
        products.forEach(p -> {
            assertEquals(ProductCategory.FOOD, p.getCategory());
            assertEquals(ProductStatus.ON_SALE, p.getStatus());
        });
    }
}