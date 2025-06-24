package com.guoshengkai.reverseapistudio.utils.script;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiatian
 */
@Slf4j
public class console {
    /**
     * 打印消息到控制台
     * @param message 消息内容
     */
    public static void log(Object... message) {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message) {
            sb.append(msg).append(" ");
        }
        log.info(sb.toString().trim());
    }

    /**
     * 打印错误消息到控制台
     * @param message 错误消息内容
     */
    public static void error(Object... message) {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message) {
            sb.append(msg).append(" ");
        }
        log.error(sb.toString().trim());
    }

    /**
     * 打印警告消息到控制台
     * @param message 警告消息内容
     */
    public static void warn(Object... message) {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message) {
            sb.append(msg).append(" ");
        }
        log.warn(sb.toString().trim());
    }

    /**
     * 打印调试消息到控制台
     * @param message 调试消息内容
     */
    public static void debug(Object... message) {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message) {
            sb.append(msg).append(" ");
        }
        log.debug(sb.toString().trim());
    }

    /**
     * 打印信息消息到控制台
     * @param message 信息消息内容
     */
    public static void info(Object... message) {
        log(message);
    }

    /**
     * 打印追踪消息到控制台
     * @param message 追踪消息内容
     */
    public static void trace(Object... message) {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message) {
            sb.append(msg).append(" ");
        }
        log.trace(sb.toString().trim());
    }

    /**
     * 打印消息到控制台并返回字符串
     * @param message 消息内容
     * @return 拼接后的消息字符串
     */
    public static String print(Object... message) {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message) {
            sb.append(msg).append(" ");
        }
        String output = sb.toString().trim();
        log.info(output);
        return output;
    }

    /**
     * 打印错误消息到控制台并返回字符串
     * @param message 错误消息内容
     * @return 拼接后的错误消息字符串
     */
    public static String printError(Object... message) {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message) {
            sb.append(msg).append(" ");
        }
        String output = sb.toString().trim();
        log.error(output);
        return output;
    }
}
