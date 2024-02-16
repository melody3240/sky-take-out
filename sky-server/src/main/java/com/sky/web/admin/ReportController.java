package com.sky.web.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("turnoverStatistics")
    public Result turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin, end);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("userStatistics")
    public Result userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        UserReportVO userReportVO = reportService.getUserStatistics(begin,end);
        return Result.success(userReportVO);
    }

    @GetMapping("ordersStatistics")
    public Result ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        OrderReportVO orderReportVO = reportService.getOrdersStatistics(begin,end);
        return Result.success(orderReportVO);
    }

    @GetMapping("top10")
    public Result top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        SalesTop10ReportVO salesTop10ReportVO = reportService.getTop10(begin,end);
        return Result.success(salesTop10ReportVO);
    }
}
