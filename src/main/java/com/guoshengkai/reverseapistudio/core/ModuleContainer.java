package com.guoshengkai.reverseapistudio.core;

import com.guoshengkai.reverseapistudio.Common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * ModuleContainer 类用于存储和管理模块信息。
 * @author xiatian
 */
public class ModuleContainer {

    private static final Map<String, EndpointContainer> endpointContainers = new HashMap<>();

    public static void initialize() {
        endpointContainers.forEach((key, value) -> {
            value.unload();
        });
        endpointContainers.clear();

        File file = new File(Common.STORE_PATH, "0");
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                String modelName = f.getName();
                EndpointContainer endpointContainer = new EndpointContainer(modelName);
                endpointContainers.put(modelName, endpointContainer);
                endpointContainer.load();
            }
        }
    }

    public static EndpointContainer getEndpointContainer(String modelName) {
        return endpointContainers.get(modelName);
    }

}
