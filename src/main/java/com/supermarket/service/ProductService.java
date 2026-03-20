package com.supermarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.dto.ProductDTO;
import com.supermarket.entity.Product;
import com.supermarket.vo.ProductVO;

import java.util.List;

public interface ProductService extends IService<Product> {

    void addProduct(ProductDTO dto);

    void updateProduct(ProductDTO dto);

    void deleteProduct(Long id);

    ProductVO getProductById(Long id);

    Page<ProductVO> getProductPage(Integer page, Integer size, String name, Long categoryId, String status);

    List<ProductVO> getProductList();

    void updateStock(Long productId, Integer quantity);
}
