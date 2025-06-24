package com.guoshengkai.reverseapistudio.utils;

import org.graalvm.polyglot.Value;

public interface ScriptRunable {
    Object run(Value js, Object... args) throws Exception;
}
