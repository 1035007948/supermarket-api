package com.supermarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotBlank(message = "商品编码不能为空")
    private String code;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotNull(message = "售价不能为空")
    @Positive(message = "售价必须大于0")
    private BigDecimal price;

    private BigDecimal costPrice;

    @NotNull(message = "库存不能为空")
    @PositiveOrZero(message = "库存不能为负数")
    private Integer stock;

    private String specification;
    private String unit;
    private String description;
    private String mainImage;
    private String images;

    @NotBlank(message = "商品状态不能为空")
    private String status;
}
