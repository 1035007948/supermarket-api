package com.supermarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.dto.CategoryDTO;
import com.supermarket.entity.Category;
import com.supermarket.vo.CategoryVO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    void addCategory(CategoryDTO dto);

    void updateCategory(CategoryDTO dto);

    void deleteCategory(Long id);

    CategoryVO getCategoryById(Long id);

    List<CategoryVO> getCategoryList();
}
