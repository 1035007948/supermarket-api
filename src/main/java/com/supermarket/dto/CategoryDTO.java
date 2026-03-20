package com.supermarket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotBlank(message = "分类编码不能为空")
    private String code;

    private Integer sortOrder;
    private Integer status;
}
