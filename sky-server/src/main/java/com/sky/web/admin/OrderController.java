package com.sky.web.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("conditionSearch")
    public Result conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("statistics")
    public Result statistics(){
        OrderStatisticsVO statisticsVO = orderService.statistics();
        return Result.success(statisticsVO);
    }

    @GetMapping("details/{id}")
    public Result details(@PathVariable Long id){
        OrderVO orderVO = orderService.findDetails(id);
        return Result.success(orderVO);
    }

    @PutMapping("cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("complete/{id}")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }

    @PutMapping("rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("confirm")
    public Result confirm(@RequestBody Long id){
        orderService.confirm(id);
        return Result.success();
    }

    @PutMapping("delivery/{id}")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }
}
