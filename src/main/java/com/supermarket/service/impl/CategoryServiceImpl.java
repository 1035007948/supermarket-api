package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.BusinessException;
import com.supermarket.dto.CategoryDTO;
import com.supermarket.entity.Category;
import com.supermarket.mapper.CategoryMapper;
import com.supermarket.service.CategoryService;
import com.supermarket.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public void addCategory(CategoryDTO dto) {
        Category exist = getOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getCode, dto.getCode()));
        if (exist != null) {
            throw new BusinessException("分类编码已存在");
        }

        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        category.setStatus(1);
        save(category);
    }

    @Override
    public void updateCategory(CategoryDTO dto) {
        Category category = getById(dto.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        Category exist = getOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getCode, dto.getCode())
                .ne(Category::getId, dto.getId()));
        if (exist != null) {
            throw new BusinessException("分类编码已存在");
        }

        BeanUtils.copyProperties(dto, category);
        updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        removeById(id);
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category category = getById(id);
        if (category == null) {
            return null;
        }
        return convertToVO(category);
    }

    @Override
    public List<CategoryVO> getCategoryList() {
        List<Category> list = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder));
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
