package com.guoshengkai.reverseapistudio.core;

import com.alibaba.fastjson.JSON;
import com.guoshengkai.reverseapistudio.Common;
import com.guoshengkai.reverseapistudio.core.entity.ApiEndpoint;
import com.guoshengkai.reverseapistudio.utils.ScriptUtil;
import lombok.Getter;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.Proxy;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * EndpointContainer 类用于管理 API 端点的容器。
 * 包含了端点列表和端点映射。
 * 提供了卸载端点的方法。
 *
 * @author Guo Shengkai
 */
public class EndpointContainer {

    @Getter
    private List<ApiEndpoint> endpoints = new LinkedList<>();

    /**
     * 存储端点的唯一标识符和 ApiEndpoint 对象的映射
     */
    private Map<String, ApiEndpoint> endpointsMap = new HashMap<>();

    /**
     * 存储端点路径和模型名称的映射
     * 例如：{"/api/v1": {"model1": [ApiEndpoint1, ApiEndpoint2], "model2": [ApiEndpoint3]}}
     */
    private Map<String, Map<String, List<ApiEndpoint>>> endpointMap = new HashMap<>();

    private String modelName;

    public EndpointContainer(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 卸载端点
     * @param key 端点的唯一标识符
     */
    public synchronized void unloadEndpoint(String key) {
        if (!endpointsMap.containsKey(key)) {
            return;
        }
        endpointsMap.remove(key);
        endpoints.removeIf(endpoint -> endpoint.getKey().equals(key));
        endpointMap.forEach((path, endpointsMap) -> {
            endpointsMap.forEach((model, endpoints) -> {
                endpoints.removeIf(endpoint -> endpoint.getKey().equals(key));
            });
        });
    }

    public synchronized void unload() {
        endpointsMap.clear();
        endpoints.clear();
        endpointMap.clear();
    }

    public void load() {
        load(null);
    }

    public void load(String path) {
        File rootDir = new File(new File(new File(Common.STORE_PATH, "0"), modelName), "apis");
        if (StringUtils.hasText(path)) {
            rootDir = new File(rootDir, path);
        }else{
            path = "/";
        }

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            return;
        }
        File[] files = rootDir.listFiles();
        if (files == null) {
            return;
        }
        for (File fileDir : files) {
            if (fileDir.isDirectory()) {
                load(fileDir.getName());
            }else if (fileDir.getName().endsWith(".mjs")) {
                loadFile(fileDir);
            }
        }
    }

    public void loadFile(File fileDir){
        ApiEndpoint apiEndpoint = new ApiEndpoint();
        File file = new File(modelName, "apis");
        String[] split = fileDir.getPath().split(file.getPath());
        apiEndpoint.setKey("//" + new File(file, split[1]).getPath());
        ScriptUtil.execute(fileDir, (js, args) -> {
            js = js.getMember("default");
            apiEndpoint.setWidget(js.getMember("widget").asInt());
            apiEndpoint.setPath(js.getMember("path").asString());
            if (!apiEndpoint.getPath().startsWith("/")) {
                apiEndpoint.setPath("/" + apiEndpoint.getPath());
            }
            apiEndpoint.setModel(js.getMember("model").asString());
            return apiEndpoint;
        });
        System.out.println(apiEndpoint);
        endpoints.add(apiEndpoint);
        endpointsMap.put(apiEndpoint.getKey(), apiEndpoint);
        endpointMap.computeIfAbsent(apiEndpoint.getPath(), k -> new HashMap<>())
                .computeIfAbsent(apiEndpoint.getModel(), k -> new ArrayList<>())
                .add(apiEndpoint);
    }

    public List<ApiEndpoint> getEndpointsByPath(String path, String model) {
        if (!endpointMap.containsKey(path)) {
            return Collections.emptyList();
        }
        Map<String, List<ApiEndpoint>> modelEndpoints = endpointMap.get(path);
        if (!modelEndpoints.containsKey(model)) {
            return Collections.emptyList();
        }
        return modelEndpoints.get(model);
    }

}
