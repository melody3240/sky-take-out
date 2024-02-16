package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.ThreadLocalUtil;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(jwtProperties.getUserTokenName());

        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecret(), token);
            Long userId = Long.parseLong(claims.get(JwtClaimsConstant.USER_ID).toString());
            ThreadLocalUtil.setCurrentId(userId);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ThreadLocalUtil.removeCurrentId();
    }
}
