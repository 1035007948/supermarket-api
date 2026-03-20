package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductVO {
    private Long id;
    private String name;
    private String code;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer stock;
    private String specification;
    private String unit;
    private String description;
    private String mainImage;
    private String images;
    private String status;
    private String statusName;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
