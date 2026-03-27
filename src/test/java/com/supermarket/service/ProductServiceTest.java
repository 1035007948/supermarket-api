package com.supermarket.service;

import com.supermarket.common.BusinessException;
import com.supermarket.dto.ProductRequest;
import com.supermarket.dto.ProductResponse;
import com.supermarket.entity.ProductCategory;
import com.supermarket.entity.ProductStatus;
import com.supermarket.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setName("测试商品");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setStock(100);
        productRequest.setSpecification("500g");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setStatus(ProductStatus.ON_SALE);
        productRequest.setDescription("测试商品描述");
    }

    @Test
    @DisplayName("创建商品-成功")
    void createProduct_Success() {
        ProductResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("测试商品", response.getName());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
        assertEquals(100, response.getStock());
        assertEquals(ProductCategory.FOOD, response.getCategory());
        assertEquals(ProductStatus.ON_SALE, response.getStatus());
    }

    @Test
    @DisplayName("获取商品详情-成功")
    void getProductById_Success() {
        ProductResponse created = productService.createProduct(productRequest);
        ProductResponse response = productService.getProductById(created.getId());

        assertNotNull(response);
        assertEquals(created.getId(), response.getId());
        assertEquals("测试商品", response.getName());
    }

    @Test
    @DisplayName("获取商品详情-商品不存在")
    void getProductById_NotFound() {
        assertThrows(BusinessException.class, () -> {
            productService.getProductById(999L);
        });
    }

    @Test
    @DisplayName("获取所有商品-分页")
    void getAllProducts_Pagination() {
        productService.createProduct(productRequest);
        productService.createProduct(productRequest);

        Page<ProductResponse> page = productService.getAllProducts(PageRequest.of(0, 10));

        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 2);
    }

    @Test
    @DisplayName("按分类查询商品")
    void getProductsByCategory() {
        productService.createProduct(productRequest);

        Page<ProductResponse> page = productService.getProductsByCategory(
                ProductCategory.FOOD, 
                PageRequest.of(0, 10)
        );

        assertTrue(page.getTotalElements() > 0);
        page.getContent().forEach(p -> assertEquals(ProductCategory.FOOD, p.getCategory()));
    }

    @Test
    @DisplayName("更新商品-成功")
    void updateProduct_Success() {
        ProductResponse created = productService.createProduct(productRequest);

        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("更新商品");
        updateRequest.setPrice(new BigDecimal("199.99"));
        updateRequest.setStock(50);
        updateRequest.setSpecification("1kg");
        updateRequest.setCategory(ProductCategory.DIGITAL);
        updateRequest.setStatus(ProductStatus.OFF_SHELF);

        ProductResponse response = productService.updateProduct(created.getId(), updateRequest);

        assertEquals("更新商品", response.getName());
        assertEquals(new BigDecimal("199.99"), response.getPrice());
        assertEquals(50, response.getStock());
        assertEquals(ProductCategory.DIGITAL, response.getCategory());
        assertEquals(ProductStatus.OFF_SHELF, response.getStatus());
    }

    @Test
    @DisplayName("删除商品-成功")
    void deleteProduct_Success() {
        ProductResponse created = productService.createProduct(productRequest);

        productService.deleteProduct(created.getId());

        assertFalse(productRepository.existsById(created.getId()));
    }

    @Test
    @DisplayName("更新库存-增加")
    void updateStock_Increase() {
        ProductResponse created = productService.createProduct(productRequest);

        ProductResponse response = productService.updateStock(created.getId(), 50);

        assertEquals(150, response.getStock());
    }

    @Test
    @DisplayName("更新库存-减少")
    void updateStock_Decrease() {
        ProductResponse created = productService.createProduct(productRequest);

        ProductResponse response = productService.updateStock(created.getId(), -30);

        assertEquals(70, response.getStock());
    }

    @Test
    @DisplayName("更新库存-库存不足")
    void updateStock_InsufficientStock() {
        ProductResponse created = productService.createProduct(productRequest);

        assertThrows(BusinessException.class, () -> {
            productService.updateStock(created.getId(), -150);
        });
    }

    @Test
    @DisplayName("搜索商品")
    void searchProducts() {
        productRequest.setName("苹果手机");
        productService.createProduct(productRequest);

        Page<ProductResponse> page = productService.searchProducts("苹果", PageRequest.of(0, 10));

        assertTrue(page.getTotalElements() > 0);
        page.getContent().forEach(p -> assertTrue(p.getName().contains("苹果")));
    }
}
