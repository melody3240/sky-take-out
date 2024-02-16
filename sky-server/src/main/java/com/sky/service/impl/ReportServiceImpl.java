package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 1.拼接一个时间区间  5月1号 -  5月30号
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 2.声明 营业额集合
        List<Double> turnoverList = new ArrayList<>();
        // 3.遍历时间
        dateList.forEach(date->{
            // 3-1 查询当天的营业额
            Double turnover = orderMapper.sumTurnover(Orders.COMPLETED,date);
            // 3-2 添加营业额
            if (turnover==null) {
                turnover=0d;
            }
            // 4.封装并返回vo
            turnoverList.add(turnover);
        });
        // 4.封装并返回vo
        return TurnoverReportVO.builder()
                .dateList(StrUtil.join(",", dateList))
                .turnoverList(StrUtil.join(",", turnoverList))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        /*
        ├─ dateList	string	必须		日期列表，以逗号分隔
        ├─ newUserList	string	必须		新增用户数列表，以逗号分隔
        ├─ totalUserList	string	必须		总用户量列表，以逗号分隔
        * */
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        ArrayList<Integer> newUserList = new ArrayList<>();
        ArrayList<Integer> totalUserList = new ArrayList<>();
        Integer totalUser = 0;
        for (LocalDate date : dateList) {
            Integer newUserCount = userMapper.findNewUserCount(date);
            newUserList.add(newUserCount);
            totalUser+=newUserCount;
            totalUserList.add(totalUser);
        }

        return UserReportVO.builder()
                .newUserList(StrUtil.join(",",newUserList))
                .dateList(StrUtil.join(",",dateList))
                .totalUserList(StrUtil.join(",",totalUserList))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        /*
        ├─ dateList	string	必须		日期列表，以逗号分隔
        ├─ orderCompletionRate	number	必须		订单完成率	format: double
        ├─ orderCountList	string	必须		订单数列表，以逗号分隔
        ├─ totalOrderCount	integer	必须		订单总数	format: int32
        ├─ validOrderCount	integer	必须		有效订单数	format: int32
        ├─ validOrderCountList	string	必须		有效订单数列表，以逗号分隔
        */
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int validOrderCount = 0;

        for (LocalDate date : dateList) {
            Integer validOrderCountOneDay = orderMapper.findOrderCount(date, Orders.COMPLETED);
            validOrderCountList.add(validOrderCountOneDay);
            Integer orderCountOneDay = orderMapper.findOrderCount(date,null);
            orderCountList.add(orderCountOneDay);
            totalOrderCount += orderCountOneDay;
            validOrderCount += validOrderCountOneDay;
        }

        BigDecimal divide = BigDecimal.valueOf(validOrderCount).divide(BigDecimal.valueOf(totalOrderCount), 2, RoundingMode.HALF_UP);
        double orderCompletionRate = divide.doubleValue();


        return OrderReportVO.builder()
                .orderCountList(StrUtil.join(",", orderCountList))
                .orderCompletionRate(orderCompletionRate)
                .dateList(StrUtil.join(",", dateList))
                .totalOrderCount(totalOrderCount)
                .validOrderCountList(StrUtil.join(",", validOrderCountList))
                .validOrderCount(validOrderCount)
                .build();
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        /*
        ├─ nameList	string	必须		商品名称列表，以逗号分隔
        ├─ numberList	string	必须		销量列表，以逗号分隔
        * */
        List<Map<String,Object>> top10 = orderMapper.findTop10(begin,end);

        List<Object> nameList= top10.stream().map(map-> map.get("name")).collect(Collectors.toList());
        List<Object> numberList= top10.stream().map(map->map.get("number")).collect(Collectors.toList());
        return SalesTop10ReportVO.builder()
                .nameList(StrUtil.join(",",nameList))
                .numberList(StrUtil.join(",",numberList))
                .build();
    }
}
