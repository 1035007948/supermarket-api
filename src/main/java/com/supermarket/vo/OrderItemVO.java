package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Long id;
    private Long productId;
    private String productName;
    private String productCode;
    private String productImage;
    private String specification;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
