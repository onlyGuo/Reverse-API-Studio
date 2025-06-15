package com.guoshengkai.reverseapistudio.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * yml配置 user.xxx
 * 用于配置管理员登录
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "user")
public class DevelopmentUserConfig {

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

}
