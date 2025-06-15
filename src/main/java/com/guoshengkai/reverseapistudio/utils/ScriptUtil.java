package com.guoshengkai.reverseapistudio.utils;


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
            Util.class, DateUtil.class
    );

    public static Object execute(String script, Object... params){
        try (Context context = Context.newBuilder("js").allowAllAccess(true)
                .allowHostClassLookup(s -> {
                    for (Class<?> aClass : ALLOW_CLASSES) {
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
    public static Object execute(File file, Object... params){
        List<Class<?>> finalAllowClasses = new ArrayList<>(ALLOW_CLASSES);
        finalAllowClasses.addAll(STATIC_CLASSES);
        try (Context context = Context.newBuilder("js").allowAllAccess(true)
                .currentWorkingDirectory(file.getParentFile().getAbsoluteFile().toPath())
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
            Value js = context.eval(Source.newBuilder("js", file).mimeType("application/javascript+module").build());
            return js.execute(params);
        }
    }

    public static Object executeMember(File file, String member, Object... params){
        try (Context context = Context.newBuilder("js").allowAllAccess(true)
                .currentWorkingDirectory(file.getParentFile().toPath())
                .allowHostClassLookup(s -> {
                    for (Class<?> aClass : ALLOW_CLASSES) {
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
            Value js = context.eval("js", "import api from '" + file.getName() + "'; api");
            return js.execute(params);
        }
    }

    public static void main(String[] args) {

    }
}
