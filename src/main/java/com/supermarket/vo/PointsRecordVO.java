package com.supermarket.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsRecordVO {
    private Long id;
    private Long memberId;
    private String type;
    private String typeName;
    private Integer points;
    private Integer balance;
    private String source;
    private String remark;
    private LocalDateTime createTime;
}
