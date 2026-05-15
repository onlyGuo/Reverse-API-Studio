package com.guoshengkai.reverseapistudio.utils.script;

import com.alibaba.fastjson.JSON;
import com.guoshengkai.reverseapistudio.utils.DateUtil;
import com.guoshengkai.reverseapistudio.utils.ThreadUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class SSE {

    private Writer writer;

    private String model;

    private String id;

    public SSE(Writer writer, String model) {
        this.writer = writer;
        this.model = model;
    }

    /**
     * 发送 OpenAI 消息
     * @param message 消息内容
     * @throws IOException 如果写入失败
     */
    public void sendOpenAIMessage(String message) throws IOException {
        String model = ThreadUtil.getCacheData("gpt_model");
        if (model == null) {
            model = this.model;
        }
        sendOpenAIMessage("assistant", message, model);
    }

    /**
     * 发送 OpenAI 消息
     * @param role 角色（如 "user" 或 "assistant"）
     * @param message 消息内容
     * @param model 模型名称
     * @throws IOException 如果写入失败
     */
    public void sendOpenAIMessage(String role, String message, String model) throws IOException {
        if (id == null) {
            id = ThreadUtil.getCacheData("gpt_id");
        }
        if (id == null) {
            id = "chatcmpl-" + UUID.randomUUID().toString().replace("-", "");
        }
        String systemFingerprint = ThreadUtil.getCacheData("gpt_system_fingerprint");
        if (systemFingerprint == null) {
            systemFingerprint = UUID.randomUUID().toString().replace("-", "");
            ThreadUtil.setCacheData("gpt_system_fingerprint", systemFingerprint);
        }
        char[] charArray = message.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            Map<String, Object> index = new LinkedHashMap<>(Map.of(
                    "index", 0,
                    "delta", Map.of(
                            "role", role,
                            "content", charArray[i]
                    )
            ));
            index.put("finish_reason", null);
            index.put("logprobs", null);
            sendObject(new LinkedHashMap<>(Map.of(
                    "id", id,
                    "object", "chat.completion.chunk",
                    "model", model,
                    "choices", List.of(index),
                    "created", System.currentTimeMillis() / 1000L,
                    "system_fingerprint", systemFingerprint
            )));
            // 模拟流式传输的延迟
            ThreadUtil.sleep(15);
        }

    }

    /**
     * 发送 自定义对象
     * @throws IOException 如果写入失败
     */
    public void sendObject(Object object) throws IOException {
        writer.write("data: " + JSON.toJSONString(object) + "\n\n");
        writer.flush();
    }

    public void done() throws IOException {
        writer.write("data: [DONE]\n\n");
        writer.flush();
        writer.close();
    }

    public void sendOpenAIUsage(Map<String, Integer> usage) throws IOException {
        if (id == null) {
            id = ThreadUtil.getCacheData("gpt_id");
        }
        if (id == null) {
            id = "chatcmpl-" + UUID.randomUUID().toString().replace("-", "");
        }
        String systemFingerprint = ThreadUtil.getCacheData("gpt_system_fingerprint");
        if (systemFingerprint == null) {
            systemFingerprint = UUID.randomUUID().toString().replace("-", "");
        }
        Map<String, Integer> index = new LinkedHashMap<>();
        index.put("prompt_tokens", usage.getOrDefault("promptTokens", 0));
        index.put("completion_tokens", usage.getOrDefault("completionTokens", 0));
        index.put("total_tokens", usage.getOrDefault("totalTokens",
                index.get("prompt_tokens") + index.get("completion_tokens")));
        sendObject(Map.of(
                "id", id,
                "object", "chat.completion.usage",
                "usage", index,
                "created", System.currentTimeMillis() / 1000L,
                "model", this.model,
                "system_fingerprint", systemFingerprint
        ));
    }
    public static void main(String[] args) {
        System.out.println(DateUtil.format(1750744845));
    }

}
