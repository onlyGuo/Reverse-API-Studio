package com.guoshengkai.reverseapistudio.utils;

import java.util.List;

/**
 * 脚本静态类枚举
 * @author xiatian
 */

public enum ScriptStaticClass {
    List(List.class),;

    private Class<?> clazz;

    ScriptStaticClass(Class<?> listClass) {
        this.clazz = listClass;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
