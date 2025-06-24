package com.guoshengkai.reverseapistudio.utils;


import com.guoshengkai.reverseapistudio.utils.script.console;
import com.guoshengkai.reverseapistudio.utils.script.Util;
import lombok.SneakyThrows;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.util.*;

/**
 * 脚本执行工具类
 * 允许在脚本中使用的类列表包括：ArrayList, LinkedList, LinkedHashMap, HashMap, List, Map
 * 静态类列表包括：Util, DateUtil
 *
 * @author gsk
 */
public class ScriptUtil {

    /**
     * 允许在脚本中使用的类列表
     */
    private static final List<Class<?>> ALLOW_CLASSES = Arrays.asList(
            ArrayList.class,
            LinkedList.class,
            LinkedHashMap.class,
            HashMap.class,
            List.class,
            Map.class
    );

    private static final List<Class<?>> STATIC_CLASSES = Arrays.asList(
            Util.class, DateUtil.class, console.class
    );

    public static Object execute(String script, Object... params){
        List<Class<?>> finalAllowClasses = new ArrayList<>(ALLOW_CLASSES);
        finalAllowClasses.addAll(STATIC_CLASSES);
        try (Context context = Context.newBuilder("js").allowAllAccess(true)
                .allowHostClassLookup(s -> {
                    for (Class<?> aClass : finalAllowClasses) {
                        if (aClass.getName().equals(s)) {
                            return true;
                        }
                    }
                    return false;
                }).build()) {
            STATIC_CLASSES.forEach(clazz -> {
                context.getBindings("js").putMember(clazz.getSimpleName(),
                        context.eval("js", "Java.type('" + clazz.getName() + "')"));
            });
            Value js = context.eval("js", script);
            return js.execute(params);
        }
    }

    @SneakyThrows
    public static Object execute(File file, ScriptRunable runable){
        List<Class<?>> finalAllowClasses = new ArrayList<>(ALLOW_CLASSES);
        finalAllowClasses.addAll(STATIC_CLASSES);
        try (Context context = Context.newBuilder("js").allowAllAccess(true)
                .currentWorkingDirectory(file.getParentFile().getAbsoluteFile().toPath())
                .option("js.esm-eval-returns-exports", "true")
                .allowHostClassLookup(s -> {
                    for (Class<?> aClass : finalAllowClasses) {
                        if (aClass.getName().equals(s)) {
                            return true;
                        }
                    }
                    return false;
                }).build()) {
            STATIC_CLASSES.forEach(clazz -> {
                context.getBindings("js").putMember(clazz.getSimpleName(),
                        context.eval("js", "Java.type('" + clazz.getName() + "')"));
            });
            Source js = Source.newBuilder("js", """
                    export class ApiEndpoint {
                        constructor(attr) {
                            this.model = attr.model;
                            this.path = attr.path;
                            this.widget = attr.widget;
                            this.service = attr.service;
                        }
                    }
                    """, "test.mjs").mimeType("application/javascript+module").build();
            Value eval = context.eval(js).getMember("ApiEndpoint");
            context.getBindings("js").putMember("ApiEndpoint", eval);
            Value js1 = context.eval(Source.newBuilder("js", file).mimeType("application/javascript+module").build());
            return runable.run(js1);
        }
    }


    public static void main(String[] args) {
        Object execute = execute("""
                /**
                 * @type {ModelApi}
                 * API接口文件 - test
                 * @author <Enter your name>
                 * @date 2025-06-23 11:16:47
                 */
                export default {
                    model: 'test',
                    path: 'v1/chat/completions',
                    widget: 1,
                    service(messages, option) {\s
                
                    }
                }
                """);
    }
}
