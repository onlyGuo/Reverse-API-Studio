package com.guoshengkai.reverseapistudio.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guoshengkai.reverseapistudio.Common;
import com.guoshengkai.reverseapistudio.core.EndpointContainer;
import com.guoshengkai.reverseapistudio.core.ModuleContainer;
import com.guoshengkai.reverseapistudio.core.entity.ApiEndpoint;
import com.guoshengkai.reverseapistudio.utils.ScriptUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ApiRequestController 用于处理所有未被其他控制器处理的请求。
 * 该控制器可以用于记录请求日志或进行全局请求处理。
 * 注意：此控制器会拦截所有路径的请求。
 * @author Guo Shengkai
 */
@Controller
@Slf4j
public class ApiRequestController {

    @PostConstruct
    private void init(){
        ModuleContainer.initialize();
    }

    /**
     * 处理所有请求
     * 注意：此方法会拦截所有未被其他控制器处理的请求。
     * 如果需要特定的处理逻辑，请在此方法中添加相应的代码。
     *
     * @param request  HttpServletRequest 对象
     * @param response HttpServletResponse 对象
     */
    @SneakyThrows
    @PostMapping("**")
    public void requestHandler(@RequestBody JSONObject body, HttpServletRequest request, HttpServletResponse response) {
        EndpointContainer defaultModule = ModuleContainer.getEndpointContainer("DefaultModule");
        String uri = request.getRequestURI();
        String model = body.getString("model");
        List<ApiEndpoint> endpoints = defaultModule.getEndpointsByPath(uri, model);
        if (endpoints == null || endpoints.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            // 内部错误, 在uri中, 未找到model端点
            String errorMessage = String.format("No endpoint found for path: %s with model: %s", uri, model);
            response.getWriter().write("{\"error\": \"%s\"}".formatted(errorMessage));
            return;
        }
        // 根据权重随机选择一个端点, 权重越高, 被选中的概率越大
        List<ApiEndpoint> weightedEndpoints = new ArrayList<>();
        for (ApiEndpoint endpoint : endpoints) {
            int count = endpoint.getWidget();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    weightedEndpoints.add(endpoint);
                }
            }
        }
        ApiEndpoint endpoint = weightedEndpoints.get((int) (Math.random() * weightedEndpoints.size()));

        log.info("Request: {}", endpoint);

        File rootDir = new File(Common.STORE_PATH, "0");
        JSONArray messages = body.getJSONArray("messages");
        body.remove("messages");
        body.put("stream", body.getOrDefault("stream", false));
        ScriptUtil.execute(new File(rootDir, endpoint.getKey()), (js, args) -> {
            Value json = js.getContext().eval("js", "JSON;");
            Value jsonParse = json.getMember("parse");
            Value jsMessages = jsonParse.execute(messages.toJSONString());
            Value jsBody = jsonParse.execute(body.toJSONString());
            Value value = js.getMember("default").getMember("service").execute(jsMessages, jsBody);
            if (value != null) {
                if (value.isHostObject()) {
                    return value.asHostObject();
                } else if (value.isString()) {
                    return value.asString();
                } else if (value.isNumber()) {
                    return value.asDouble();
                } else if (value.isBoolean()) {
                    return value.asBoolean();
                } else if (value.hasArrayElements()) {
                    return JSON.parseArray(value.toString(), Object.class);
                }else if (value.hasMembers()) {
                    return JSON.parseObject(value.toString());
                } else {
                    return value;
                }
            }
            return null;
        });
    }

}
