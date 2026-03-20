package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.dto.CategoryDTO;
import com.supermarket.service.CategoryService;
import com.supermarket.vo.CategoryVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<Void> add(@RequestBody @Valid CategoryDTO dto) {
        categoryService.addCategory(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid CategoryDTO dto) {
        dto.setId(id);
        categoryService.updateCategory(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CategoryVO> getById(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @GetMapping("/list")
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.getCategoryList());
    }
}
