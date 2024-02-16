package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import com.sky.vo.UserLoginVO;

import java.util.List;

public interface UserService {
    UserLoginVO login(UserLoginDTO userLoginDTO);
}
