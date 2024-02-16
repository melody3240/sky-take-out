package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    OrderVO findDetails(Long id);

    void cancel(OrdersCancelDTO ordersCancelDTO);

    void complete(Long id);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void confirm(Long id);

    void delivery(Long id);

    OrderSubmitVO save(OrdersSubmitDTO ordersSubmitDTO);

    // 订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    // 支付成功，修改订单状态
    void paySuccess(String outTradeNo);

    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    void userOrderCancel(Long id);

    void repetition(Long id);

    void reminder(Long id);
}
