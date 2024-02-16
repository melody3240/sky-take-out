package com.sky.task;

import cn.hutool.core.collection.CollUtil;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    // 每分钟处理一次订单超时时间
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder(){
        // 获取15分钟前的时间
        LocalDateTime old15Time = LocalDateTime.now().minusMinutes(15);
        // 查询未支付订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT,old15Time);
        // 遍历更改状态为取消
        if(CollUtil.isNotEmpty(ordersList)){
            ordersList.forEach(orders->{
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("用户超时未支付");
                orderMapper.update(orders);
            });
        }
    }

    // 每天凌晨1点处理前一天的配送中订单
    @Scheduled(cron = "0 0 1 * * ?")
    public void processToOver(){
        // 获取60分钟前的时间
        LocalDateTime old60Time = LocalDateTime.now().minusMinutes(60);
        // 查询配送中订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS,old60Time);
        // 遍历更改状态为取消
        ordersList.forEach(orders->{
            orders.setStatus(Orders.COMPLETED);
            orderMapper.update(orders);
            log.info("处理配送订单号为："+orders.getNumber());
        });
    }

}
