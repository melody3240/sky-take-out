package com.sky.web.app;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/order")
public class AppOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("submit")
    public Result submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        //1.调用service完成保存订单操作,返回OrderSubmitVo
        OrderSubmitVO vo = orderService.save(ordersSubmitDTO);

        //2.将vo封装成Result且返回
        return Result.success(vo);
    }

    // 订单支付
    @PutMapping("/payment")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("historyOrders")
    public Result historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.historyOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("orderDetail/{id}")
    public Result findOrderDetailById(@PathVariable Long id){
        OrderVO orderVO = orderService.findDetails(id);
        return Result.success(orderVO);
    }

    @PutMapping("cancel/{id}")
    public Result cancelOrder(@PathVariable Long id){
        orderService.userOrderCancel(id);
        return Result.success();
    }

    // 再来一单
    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable("id") Long id) {
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable("id")Long id){
        orderService.reminder(id);
        return Result.success();
    }
}
