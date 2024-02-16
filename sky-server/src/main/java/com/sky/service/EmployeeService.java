package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.vo.EmployeeLoginVO;

public interface EmployeeService {

    EmployeeLoginVO login(EmployeeLoginDTO loginDTO);

    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    void insert(EmployeeDTO employeeDTO);

    Employee findById(Long id);

    void updateById(EmployeeDTO employeeDTO);

    void checkStatus(Integer status, Long id);
}
