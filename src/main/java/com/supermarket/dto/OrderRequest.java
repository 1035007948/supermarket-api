package com.supermarket.dto;

import com.supermarket.entity.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    
    @NotNull(message = "支付方式不能为空")
    private PaymentMethod paymentMethod;
    
    @Valid
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemRequest> items;
    
    private Integer pointsUsed;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverAddress;
    
    private String deliveryType;
    
    private String remark;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量至少为1")
        private Integer quantity;
    }
}
