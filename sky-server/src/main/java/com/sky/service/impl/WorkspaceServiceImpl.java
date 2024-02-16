package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public BusinessDataVO businessData(LocalDate now) {
        /*
        ├─ newUsers	integer	必须		新增用户数	format: int32
        ├─ orderCompletionRate	number	必须		订单完成率	format: double
        ├─ turnover	number	必须		营业额	format: double
        ├─ unitPrice	number	必须		平均客单价	format: double
        ├─ validOrderCount	integer	必须		有效订单数	format: int32
        */
        Integer newUsers = userMapper.findNewUserCount(now);
        Double turnover = orderMapper.sumTurnover(Orders.COMPLETED, now);
        Integer validOrderCount = orderMapper.findOrderCount(now, Orders.COMPLETED);
        Integer orderCount = orderMapper.findOrderCount(now, null);
        double orderCompletionRate = .0;
        double unitPrice = .0;
        if(orderCount != 0){
            orderCompletionRate = BigDecimal.valueOf(validOrderCount).divide(BigDecimal.valueOf(orderCount),2, RoundingMode.HALF_UP).doubleValue();
        }
        if(validOrderCount != 0){
            unitPrice = BigDecimal.valueOf(turnover).divide(BigDecimal.valueOf(validOrderCount),2, RoundingMode.HALF_UP).doubleValue();
        }

        return BusinessDataVO.builder()
                .unitPrice(unitPrice)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .newUsers(newUsers)
                .turnover(turnover)
                .build();
    }

    @Override
    public OrderOverViewVO overviewOrders(LocalDate now) {
        return orderMapper.findCount4Status(now);
    }
}
