package com.sky.mapper;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Select("select * from employee where username = #{username}")
    Employee findByUserName(EmployeeLoginDTO loginDTO);

    List<Employee> page(String name);

    void insert(Employee employee);

    @Select("select * from employee where username = #{username}")
    Employee getByUserName(String username);

    @Select("select * from employee where phone = #{phone}")
    Employee getByPhone(String phone);

    @Select("select * from employee where id_number = #{idNumber}")
    Employee getIdNumber(String idNumber);

    @Select("select * from employee where id = #{id}")
    Employee findById(Long id);

    @Update("update employee set id_number = #{idNumber},name = #{name},phone = #{phone},sex = #{sex},username = #{username} where id = #{id}")
    void updateById(Employee employee);

    @Update("update employee set status = #{status} where id = #{id}")
    void checkStatus(Employee employee);
}
