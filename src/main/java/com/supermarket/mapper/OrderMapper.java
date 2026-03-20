package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM orders WHERE status IN ('PENDING_PICKUP', 'SHIPPED') AND notify_status = 0 AND prepared_time <= #{time}")
    List<Order> selectOrdersNeedNotify(@Param("time") LocalDateTime time);
}
