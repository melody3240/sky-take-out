package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;

import java.time.LocalDate;

public interface WorkspaceService {
    BusinessDataVO businessData(LocalDate now);

    OrderOverViewVO overviewOrders(LocalDate now);
}
