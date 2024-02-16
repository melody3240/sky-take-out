package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String password;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

    //员工id
    private Long empId;

    //旧密码
    private String oldPassword;

    //新密码
    private String newPassword;

}
