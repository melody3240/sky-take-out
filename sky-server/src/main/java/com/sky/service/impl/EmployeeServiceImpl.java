package com.sky.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.BusinessException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public EmployeeLoginVO login(EmployeeLoginDTO loginDTO) {
        String password = loginDTO.getPassword();
        String username = loginDTO.getUsername();
        if(StrUtil.isBlank(username)){
            throw new BusinessException("账号不能为空");
        }
        if(StrUtil.isBlank(password)){
            throw new BusinessException("密码不能为空");
        }

        Employee employee = employeeMapper.findByUserName(loginDTO);

        if(employee == null){
            throw new BusinessException("账号不存在");
        }

        String md5 = SecureUtil.md5(password);

        if(!md5.equals(employee.getPassword())){
            throw new BusinessException("密码错误！");
        }

        if(StatusConstant.DISABLE.equals(employee.getStatus())){
            throw new BusinessException("该账号为禁用状态！");
        }

        Map<String, Object> map = new HashMap<>();
        map.put(JwtClaimsConstant.EMP_ID,employee.getId());

        String token = JwtUtil.createJWT(jwtProperties.getAdminSecret(), jwtProperties.getAdminTtl(), map);

        return EmployeeLoginVO.builder().id(employee.getId()).name(employee.getName()).userName(employee.getUsername()).token(token).build();
    }

    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        if(employeePageQueryDTO.getPage() == 0){employeePageQueryDTO.setPage(1);}
        if(employeePageQueryDTO.getPageSize() == 0){employeePageQueryDTO.setPage(10);}

        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        List<Employee> list = employeeMapper.page(employeePageQueryDTO.getName());

        Page<Employee> page = (Page<Employee>) list;
        return new PageResult(page.getTotal(),list);
    }

    @Override
    public void insert(EmployeeDTO employeeDTO) {
        // 1.参数校验
        examine(employeeDTO);

        // 2.业务校验
        // 2-1 账号唯一
        Employee byUserName = employeeMapper.getByUserName(employeeDTO.getUsername());
        if(byUserName != null){
            throw new BusinessException("账号已存在");
        }
        // 2-2 手机号唯一
        Employee byPhone = employeeMapper.getByPhone(employeeDTO.getPhone());
        if(byPhone != null){
            throw new BusinessException("手机号已存在");
        }
        // 2-3 身份证号唯一
        Employee byIdNumber = employeeMapper.getIdNumber(employeeDTO.getIdNumber());
        if(byIdNumber != null){
            throw new BusinessException("身份证号已存在");
        }

        Employee employee = BeanUtil.copyProperties(employeeDTO,Employee.class);

        // 3-1 补全信息
        String md5 = SecureUtil.md5(PasswordConstant.DEFAULT_PASSWORD);
        employee.setPassword(md5);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(employee.getCreateTime());
        employee.setStatus(StatusConstant.DISABLE);
        employee.setCreateUser(ThreadLocalUtil.getCurrentId());
        employee.setUpdateUser(ThreadLocalUtil.getCurrentId());

        employeeMapper.insert(employee);
    }

    @Override
    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }

    @Override
    public void updateById(EmployeeDTO employeeDTO) {
        examine(employeeDTO);

        // 2.业务校验
        // 2-1 账号唯一
        Employee byUserName = employeeMapper.getByUserName(employeeDTO.getUsername());
        if(byUserName != null && !employeeDTO.getId().equals(byUserName.getId())){
            throw new BusinessException("账号已存在");
        }
        // 2-2 手机号唯一
        Employee byPhone = employeeMapper.getByPhone(employeeDTO.getPhone());
        if(byPhone != null && !employeeDTO.getId().equals(byPhone.getId())){
            throw new BusinessException("手机号已存在");
        }
        // 2-3 身份证号唯一
        Employee byIdNumber = employeeMapper.getIdNumber(employeeDTO.getIdNumber());
        if(byIdNumber != null && !employeeDTO.getId().equals(byIdNumber.getId())){
            throw new BusinessException("身份证号已存在");
        }

        Employee employee = BeanUtil.copyProperties(employeeDTO, Employee.class);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(ThreadLocalUtil.getCurrentId());
        employeeMapper.updateById(employee);
    }

    private void examine(EmployeeDTO employeeDTO) {
        // 1.参数校验
        if (
                StrUtil.isBlank(employeeDTO.getName()) ||
                        StrUtil.isBlank(employeeDTO.getUsername()) ||
                        StrUtil.isBlank(employeeDTO.getPhone()) ||
                        StrUtil.isBlank(employeeDTO.getIdNumber())
        ) {
            throw new BusinessException("参数非法");
        }

        String regPhone = "^(13[0-9]|14[579]|15[0-35-9]|166|17[01345678]|18[0-9]|19[89])\\d{8}$";
        if (!employeeDTO.getPhone().matches(regPhone)) {
            throw new BusinessException("手机号格式不合法");
        }
        String id_18 = "^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{3}([0-9]|(X|x))";
        if (!employeeDTO.getIdNumber().matches(id_18)) {
            throw new BusinessException("身份证号格式不合法");
        }
    }

    @Override
    public void checkStatus(Integer status, Long id) {
        Employee employee = Employee.builder().id(id).status(status).updateTime(LocalDateTime.now()).updateUser(ThreadLocalUtil.getCurrentId()).build();

        employeeMapper.checkStatus(employee);
    }
}
