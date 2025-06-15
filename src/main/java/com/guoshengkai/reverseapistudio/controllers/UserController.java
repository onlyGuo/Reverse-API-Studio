package com.guoshengkai.reverseapistudio.controllers;

import com.guoshengkai.reverseapistudio.conf.DevelopmentUserConfig;
import com.guoshengkai.reverseapistudio.exception.ValidationException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Resource
    private DevelopmentUserConfig developmentUserConfig;

    @PostMapping("login")
    public Map<String, Object> login(@RequestBody DevelopmentUserConfig userConfig) {
        if (!developmentUserConfig.getUsername().equals(userConfig.getUsername())
                || !developmentUserConfig.getPassword().equals(userConfig.getPassword())) {
            throw new ValidationException("Login Failed");
        }
        return Map.of("status", "success");
    }

}
