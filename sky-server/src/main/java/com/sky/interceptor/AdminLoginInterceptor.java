package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.ThreadLocalUtil;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AdminLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");

        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecret(), token);
            long empId = Long.parseLong(claims.get(JwtClaimsConstant.EMP_ID).toString());
            ThreadLocalUtil.setCurrentId(empId);
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ThreadLocalUtil.removeCurrentId();
    }
}
