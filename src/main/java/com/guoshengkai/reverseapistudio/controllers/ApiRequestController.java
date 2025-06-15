package com.guoshengkai.reverseapistudio.controllers;

import com.guoshengkai.reverseapistudio.core.ModuleContainer;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ApiRequestController 用于处理所有未被其他控制器处理的请求。
 * 该控制器可以用于记录请求日志或进行全局请求处理。
 * 注意：此控制器会拦截所有路径的请求。
 * @author Guo Shengkai
 */
@Controller
public class ApiRequestController {

    @PostConstruct
    private void init(){
//        ModuleContainer.getEndpointContainer("DefaultModule");
    }

    /**
     * 处理所有请求
     * 注意：此方法会拦截所有未被其他控制器处理的请求。
     * 如果需要特定的处理逻辑，请在此方法中添加相应的代码。
     *
     * @param request  HttpServletRequest 对象
     * @param response HttpServletResponse 对象
     */
    @PostMapping("**")
    public void requestHandler(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Received request: " + request.getRequestURI());
    }

}
