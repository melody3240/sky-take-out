package com.sky.web.admin;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("login")
    public Result login(@RequestBody EmployeeLoginDTO loginDTO){
        EmployeeLoginVO loginVO = employeeService.login(loginDTO);
        return Result.success(loginVO);
    }

    @PostMapping("logout")
    public Result logout(){
        return Result.success();
    }

    @GetMapping("page")
    public Result page(EmployeePageQueryDTO employeePageQueryDTO){
        PageResult pageResult = employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping()
    public Result insert(@RequestBody EmployeeDTO employeeDTO){
        employeeService.insert(employeeDTO);
        return Result.success();
    }

    @GetMapping("{id}")
    public Result findById(@PathVariable Long id){
        Employee employee = employeeService.findById(id);
        return Result.success(employee);
    }

    @PutMapping
    public Result updateById(@RequestBody EmployeeDTO employeeDTO){
        employeeService.updateById(employeeDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result checkStatus(@PathVariable Integer status,Long id){
        employeeService.checkStatus(status,id);
        return Result.success();
    }
}
