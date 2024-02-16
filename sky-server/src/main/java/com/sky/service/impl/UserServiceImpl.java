package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.exception.BusinessException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.DishItemVO;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    @Transactional
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        //1.参数校验(code不能为空)
        String code = userLoginDTO.getCode();
        if (StrUtil.isBlank(code)) {
            throw new BusinessException("code授权码不能为空");
        }

        //2.使用httpClient向微信接口平台发送微信登陆请求
        //2.1 设置微信登陆的url
        String url = "https://api.weixin.qq.com/sns/jscode2session";

        //2.2 设置微信登陆的4个参数,放入map中
        Map<String,String> paramMap = new HashMap<>();
        //paramMap.put("appid","wxffb3637a228223b8");
        paramMap.put("appid",weChatProperties.getAppid());
        //paramMap.put("secret","84311df9199ecacdf4f12d27b6b9522d");
        paramMap.put("secret",weChatProperties.getSecret());
        paramMap.put("js_code",code);
        paramMap.put("grant_type","authorization_code");

        //2.3 发送请求
        //{"session_key":"7g2SwxqIDO35gcwV935wCw==","openid":"osvjO5TFjtA1tdkT5XP-CnKCzZcs","unionid":"o5PB1s_3T0QxRU_F8PbHvOZS3lbw"}
        String responseJsonStr = HttpClientUtil.doGet(url, paramMap);

        //3.获取微信平台返回的openid
        Map<String,String> map = JSON.parseObject(responseJsonStr, Map.class);
        String openid = map.get("openid");

        if (StrUtil.isBlank(openid)) {
            throw new BusinessException("请重新登陆");
        }

        //4.调用mapper.findByOpenid(),返回用户
        User user = userMapper.findByOpenid(openid);

        //5.判断用户是否为空,若为空就证明第一次登陆,就往user表中插入一条数据(新增记录主键返回)
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setCreateTime(LocalDateTime.now());
            userMapper.save(user);
        }

        //6.调用JwtUtil生成token
        Map<String,Object> claims = new HashMap<>();
        //将用户的id存入claims中
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecret(), jwtProperties.getUserTtl(), claims);

        //7.封装vo且返回
        return UserLoginVO.builder()
                .id(user.getId())
                .token(token)
                .openid(openid)
                .build();
    }
}
