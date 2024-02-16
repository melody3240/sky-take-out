package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecret;
    private Long adminTtl;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecret;
    private Long userTtl;
    private String userTokenName; //登录token名称

}
