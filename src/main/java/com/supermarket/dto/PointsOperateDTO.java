package com.supermarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointsOperateDTO {
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @NotNull(message = "积分数量不能为空")
    private Integer points;

    @NotBlank(message = "操作类型不能为空")
    private String type;

    private String remark;
}
