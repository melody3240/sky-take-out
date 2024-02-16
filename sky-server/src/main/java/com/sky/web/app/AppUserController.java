package com.sky.web.app;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.vo.DishItemVO;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/user")
public class AppUserController {
    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO){
        UserLoginVO userLoginVO = userService.login(userLoginDTO);
        return Result.success(userLoginVO);
    }
}
