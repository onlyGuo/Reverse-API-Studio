package com.guoshengkai.reverseapistudio.core;

import com.alibaba.fastjson.JSON;
import com.guoshengkai.reverseapistudio.Common;
import com.guoshengkai.reverseapistudio.core.entity.ApiEndpoint;
import com.guoshengkai.reverseapistudio.utils.ScriptUtil;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
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

    private List<ApiEndpoint> endpoints = new LinkedList<>();

    private Map<String, ApiEndpoint> endpointsMap = new HashMap<>();

    private Map<String, Map<String, List<ApiEndpoint>>> endpointMap = new HashMap<>();

    private Engine engine = null;
    private Map<String, String> options = new HashMap<>();

    private String modelName;

    public EndpointContainer(String modelName) {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");

        // Enable CommonJS experimental support.
        options.put("js.commonjs-require", "true");
        // (optional) folder where the NPM modules to be loaded are located.
        options.put("js.commonjs-require-cwd", new File(Common.STORE_PATH, "0").getAbsolutePath());

        engine = Engine.newBuilder()
                .allowExperimentalOptions(true)
                .options(options)
                .build();
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
        if (engine != null) {
            engine.close();
            engine = null;
        }
        options.clear();
    }

    public void loadEndpoint(ApiEndpoint endpoint) {
        if (!endpointsMap.containsKey(endpoint.getKey())) {
            endpointsMap.put(endpoint.getKey(), endpoint);
            endpoints.add(endpoint);

            // 读文件详情 TODO 需要JS引擎
            try(Context context = Context.newBuilder()
                    .allowAllAccess(true)
                    .engine(engine)
                    .options(options)
                    .allowExperimentalOptions(true)
                    .allowIO(true)
                    .currentWorkingDirectory(Path.of(new File(new File(Common.STORE_PATH, "0"),
                            modelName).toURI()))
                    .build()) {
                String path = endpoint.getKey()
                        .replace("//", "./").replace("./", "");
                Source js = Source.newBuilder("js",
                        String.format("import api from '%s'; api", path),
                        path.replace("./", "")
                ).mimeType("application/javascript+module").build();
                Value eval = context.eval(js);
                System.out.println(JSON.toJSONString(eval.as(Object.class)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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
                ApiEndpoint apiEndpoint = new ApiEndpoint();
                apiEndpoint.setKey(path + "/" + fileDir.getName());
                Object execute = ScriptUtil.execute(fileDir);
                System.out.println(execute);
            }
        }
    }
}
