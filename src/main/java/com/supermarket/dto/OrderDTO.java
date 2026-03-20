package com.supermarket.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private Long memberId;
    private String memberName;
    private String memberPhone;

    @NotBlank(message = "支付方式不能为空")
    private String payType;

    @NotBlank(message = "配送方式不能为空")
    private String deliveryType;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private Integer pointsDeduct;

    @NotEmpty(message = "订单商品不能为空")
    @Valid
    private List<OrderItemDTO> items;
}
