package com.guoshengkai.reverseapistudio.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guoshengkai.reverseapistudio.Common;
import com.guoshengkai.reverseapistudio.core.entity.ApiEndpoint;
import com.guoshengkai.reverseapistudio.socks5.NettySocks5ProxyServerController;
import com.guoshengkai.reverseapistudio.utils.FileUtil;
import com.guoshengkai.reverseapistudio.utils.ScriptUtil;
import com.guoshengkai.reverseapistudio.utils.script.Storage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.Proxy;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

/**
 * EndpointContainer 类用于管理 API 端点的容器。
 * 包含了端点列表和端点映射。
 * 提供了卸载端点的方法。
 *
 * @author Guo Shengkai
 */
@Slf4j
public class EndpointContainer {

    @Getter
    private final List<ApiEndpoint> endpoints = new LinkedList<>();

    /**
     * 存储端点的唯一标识符和 ApiEndpoint 对象的映射
     */
    private final Map<String, ApiEndpoint> endpointsMap = new HashMap<>();

    /**
     * 存储端点路径和模型名称的映射
     * 例如：{"/api/v1": {"model1": [ApiEndpoint1, ApiEndpoint2], "model2": [ApiEndpoint3]}}
     */
    private final Map<String, Map<String, List<ApiEndpoint>>> endpointMap = new HashMap<>();

    private final String modelName;

    private final NettySocks5ProxyServerController socks5ProxyServerController;

    public EndpointContainer(String modelName) {
        this.modelName = modelName;
        socks5ProxyServerController = new NettySocks5ProxyServerController();
        reinitSocks5();
    }

    public void reinitSocks5(){
        File rootDir = new File(new File(Common.STORE_PATH, "0"), modelName);
        File configFile = new File(rootDir, "config.js");
        ScriptUtil.execute(configFile, (js, args) -> {
            js = js.getMember("default");
            Value socks = js.getMember("socks");
            if (socks != null && !socks.isNull()){
                int port = socks.getMember("port").asInt();
                boolean enable = socks.getMember("enable").asBoolean();
                socks5ProxyServerController.setState(port, enable);
                Value auth = socks.getMember("auth");
                Value toJson = js.getContext().eval("js",
                        "const __TO_JSON__ = (obj) => JSON.stringify(obj, null, 2); __TO_JSON__");
                String string = toJson.execute(auth).asString();
                JSONArray objects = JSON.parseArray(string);
                Map<String, String> authMap = new HashMap<>();
                for (int i = 0; i < objects.size(); i++) {
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String username = jsonObject.getString("username");
                    String password = jsonObject.getString("password");
                    boolean enable1 = jsonObject.getBooleanValue("enable");
                    if (StringUtils.hasText(username) && StringUtils.hasText(password) && enable1) {
                        authMap.put(username, password);
                    }
                }
                socks5ProxyServerController.updateAuth(authMap);
            }else{
                socks5ProxyServerController.setState(0, false);
            }
            return null;
        });
    }

    /**
     * 卸载端点
     * @param key 端点的唯一标识符
     */
    public synchronized void unloadEndpoint(String key) {
        if (!endpointsMap.containsKey(key)) {
            return;
        }
        try (ApiEndpoint remove = endpointsMap.remove(key)){
            endpoints.removeIf(endpoint -> endpoint.getKey().equals(key));
            endpointMap.forEach((path, endpointsMap) -> {
                endpointsMap.forEach((model, endpoints) -> {
                    endpoints.removeIf(endpoint -> endpoint.getKey().equals(key));
                });
            });
        }catch (Exception ignored){}
    }

    public synchronized void unload() {
        endpoints.forEach(ApiEndpoint::close);
        endpoints.clear();
        endpointsMap.clear();
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
        apiEndpoint.setStorage(new Storage(apiEndpoint.getKey()));
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
        if (endpointsMap.containsKey(apiEndpoint.getKey())) {
            ApiEndpoint remove = endpointsMap.remove(apiEndpoint.getKey());
            apiEndpoint.getStorage().close();
            apiEndpoint.setStorage(remove.getStorage());
            endpointMap.forEach((path, modelEndpoints) -> {
                modelEndpoints.forEach((model, endpointList) -> {
                    endpointList.removeIf(endpoint -> endpoint.getKey().equals(apiEndpoint.getKey()));
                });
            });
        }else{
            endpoints.add(apiEndpoint);
        }
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
