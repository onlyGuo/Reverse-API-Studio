package com.guoshengkai.reverseapistudio.core.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * ApiEndpoint 类表示一个 API 端点的基本信息。
 * 包含了端点的 widget 和 key 属性。
 *
 * @author Guo Shengkai
 */
@Getter
@Setter
public class ApiEndpoint {

    /**
     * 权重
     */
    private int widget;

    /**
     * 端点的唯一标识符
     */
    private String key;

}
