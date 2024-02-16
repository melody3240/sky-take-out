package com.sky.web.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/workspace")
@AllArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @GetMapping("businessData")
    public Result businessData(){
       BusinessDataVO businessDataVO = workspaceService.businessData(LocalDate.now());
        return Result.success(businessDataVO);
    }

    @GetMapping("overviewOrders")
    public Result overviewOrders(){
        OrderOverViewVO orderOverViewVO = workspaceService.overviewOrders(LocalDate.now());
        return Result.success(orderOverViewVO);
    }
}
