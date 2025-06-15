package com.guoshengkai.reverseapistudio.utils.script;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息类，用于存储消息内容和角色
 * 角色可以是 "user" 或 "assistant"
 * @author gsk
 */
@Getter
@Setter
public class Message {
    private Object content;
    private String role;
}
