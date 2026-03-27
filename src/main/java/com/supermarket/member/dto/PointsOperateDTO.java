package com.supermarket.member.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PointsOperateDTO {

    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @NotNull(message = "积分数量不能为空")
    private Integer points;

    @NotNull(message = "操作类型不能为空")
    private Integer type;

    private String orderNo;

    @NotBlank(message = "来源不能为空")
    private String source;

    private String description;
}
