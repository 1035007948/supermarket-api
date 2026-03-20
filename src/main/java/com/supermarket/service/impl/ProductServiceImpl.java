package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.BusinessException;
import com.supermarket.dto.ProductDTO;
import com.supermarket.entity.Category;
import com.supermarket.entity.Product;
import com.supermarket.mapper.CategoryMapper;
import com.supermarket.mapper.ProductMapper;
import com.supermarket.service.ProductService;
import com.supermarket.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private CategoryMapper categoryMapper;

    private static final Map<String, String> STATUS_MAP = Map.of(
            "ON_SALE", "在售",
            "OFF_SHELF", "下架",
            "OUT_OF_STOCK", "缺货",
            "PRE_SALE", "预售"
    );

    @Override
    public void addProduct(ProductDTO dto) {
        Product exist = getOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getCode, dto.getCode()));
        if (exist != null) {
            throw new BusinessException("商品编码已存在");
        }

        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        save(product);
    }

    @Override
    public void updateProduct(ProductDTO dto) {
        Product product = getById(dto.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        Product exist = getOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getCode, dto.getCode())
                .ne(Product::getId, dto.getId()));
        if (exist != null) {
            throw new BusinessException("商品编码已存在");
        }

        BeanUtils.copyProperties(dto, product);
        updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        removeById(id);
    }

    @Override
    public ProductVO getProductById(Long id) {
        Product product = getById(id);
        if (product == null) {
            return null;
        }
        return convertToVO(product);
    }

    @Override
    public Page<ProductVO> getProductPage(Integer page, Integer size, String name, Long categoryId, String status) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(name)) {
            wrapper.like(Product::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByDesc(Product::getCreateTime);
        Page<Product> productPage = page(pageParam, wrapper);

        List<ProductVO> voList = productPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<ProductVO> voPage = new Page<>();
        BeanUtils.copyProperties(productPage, voPage);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<ProductVO> getProductList() {
        List<Product> list = list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, "ON_SALE")
                .orderByDesc(Product::getCreateTime));
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public void updateStock(Long productId, Integer quantity) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new BusinessException("库存不足");
        }

        product.setStock(newStock);
        if (newStock == 0) {
            product.setStatus("OUT_OF_STOCK");
        }
        updateById(product);
    }

    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        vo.setStatusName(STATUS_MAP.getOrDefault(product.getStatus(), product.getStatus()));
        return vo;
    }
}
