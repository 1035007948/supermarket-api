package com.supermarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.PageResult;
import com.supermarket.common.Result;
import com.supermarket.dto.ProductDTO;
import com.supermarket.service.ProductService;
import com.supermarket.vo.ProductVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public Result<Void> add(@RequestBody @Valid ProductDTO dto) {
        productService.addProduct(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid ProductDTO dto) {
        dto.setId(id);
        productService.updateProduct(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getById(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    @GetMapping("/page")
    public Result<PageResult<ProductVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        Page<ProductVO> result = productService.getProductPage(page, size, name, categoryId, status);
        return Result.success(PageResult.of(result.getTotal(), (long) page, (long) size, result.getRecords()));
    }

    @GetMapping("/list")
    public Result<List<ProductVO>> list() {
        return Result.success(productService.getProductList());
    }
}
