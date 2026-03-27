package com.supermarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.dto.LoginRequest;
import com.supermarket.dto.ProductRequest;
import com.supermarket.dto.RegisterRequest;
import com.supermarket.entity.ProductCategory;
import com.supermarket.entity.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        token = objectMapper.readTree(response).path("data").path("token").asText();
    }

    @Test
    @DisplayName("创建商品-需要认证")
    void createProduct_RequiresAuth() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("测试商品");
        request.setPrice(new BigDecimal("99.99"));
        request.setStock(100);
        request.setCategory(ProductCategory.FOOD);
        request.setStatus(ProductStatus.ON_SALE);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("创建商品-成功")
    void createProduct_Success() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("测试商品");
        request.setPrice(new BigDecimal("99.99"));
        request.setStock(100);
        request.setCategory(ProductCategory.FOOD);
        request.setStatus(ProductStatus.ON_SALE);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("测试商品"));
    }

    @Test
    @DisplayName("获取商品列表-成功")
    void getAllProducts_Success() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("按分类查询商品")
    void getProductsByCategory() throws Exception {
        mockMvc.perform(get("/api/products/category/FOOD")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("搜索商品")
    void searchProducts() throws Exception {
        mockMvc.perform(get("/api/products/search")
                        .header("Authorization", "Bearer " + token)
                        .param("name", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
