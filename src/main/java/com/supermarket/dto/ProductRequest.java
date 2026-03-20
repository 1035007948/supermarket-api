package com.supermarket.dto;

import com.supermarket.entity.ProductCategory;
import com.supermarket.entity.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
    
    private String specification;
    
    @NotNull(message = "商品分类不能为空")
    private ProductCategory category;
    
    @NotNull(message = "商品状态不能为空")
    private ProductStatus status;
    
    private String description;
    
    private String imageUrl;
}
