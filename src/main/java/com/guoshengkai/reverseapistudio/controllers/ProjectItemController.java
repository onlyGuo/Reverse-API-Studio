package com.guoshengkai.reverseapistudio.controllers;

import com.guoshengkai.reverseapistudio.Common;
import com.guoshengkai.reverseapistudio.core.ModuleContainer;
import com.guoshengkai.reverseapistudio.entitys.FileItem;
import com.guoshengkai.reverseapistudio.entitys.models.ModelEntity;
import com.guoshengkai.reverseapistudio.exception.ValidationException;
import com.guoshengkai.reverseapistudio.utils.DateUtil;
import com.guoshengkai.reverseapistudio.utils.FileUtil;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Copyright (c) 2021 HEBEI CLOUD IOT FACTORY BIGDATA CO.,LTD.
 * Legal liability shall be investigated for unauthorized use
 *
 * @Author: Guo Shengkai
 * @Date: Create in 2022/5/10 10:39
 */
@RestController
@RequestMapping("/api/v1/project")
public class ProjectItemController {

    @PostMapping("/folder")
    public FileItem createFolder(@RequestBody FileItem fileInfo, String projectId) {
        File file = new File(new File(Common.STORE_PATH, projectId), fileInfo.getKey());
        if (!file.exists()) {
            if (file.mkdirs()) {
                fileInfo.setChild(new ArrayList<>());
                fileInfo.setType("dir");
                return fileInfo;
            }
            throw new RuntimeException("创建文件夹失败");
        }
        throw new RuntimeException("文件夹已存在");
    }

    /**
     * 写入文件内容
     */
    @RequestMapping("write")
    public void write(@RequestBody FileItem fileInfo, String projectId) {
        if (!StringUtils.hasLength(fileInfo.getKey())){
            throw new RuntimeException("key不能为空");
        }
        if (null == fileInfo.getContent()){
            fileInfo.setContent("");
        }
        File file = new File(new File(Common.STORE_PATH, projectId), fileInfo.getKey());
        FileUtil.writeFile(fileInfo.getContent().getBytes(StandardCharsets.UTF_8), file);
        file.setWritable(true);
        file.setReadable(true);
        file.setLastModified(System.currentTimeMillis());
        if (file.getName().toLowerCase().endsWith(".mds")){
            ModelEntity entity = FileUtil.parseMds(fileInfo.getContent());
            String moduleName = fileInfo.getKey().replace("//", "");
            moduleName = moduleName.substring(0, moduleName.indexOf('/'));
//            modelContainer.putModel(moduleName, entity.getName(), entity, projectId);
        }
    }

    @DeleteMapping("delete")
    public void delete(String path, String projectId) {
        File file = new File(new File(Common.STORE_PATH, projectId), path);
        FileUtil.rm(file);
    }

    @PostMapping("rename")
    public void rename(@RequestBody FileItem fileInfo, String projectId) {
        File file = new File(new File(Common.STORE_PATH, projectId), fileInfo.getKey());
        if (file.exists()) {
            file.renameTo(new File(file.getParentFile(), fileInfo.getName()));
        }else{
            throw new ValidationException("文件不存在");
        }
    }

    @SneakyThrows
    @PostMapping("/jsFile")
    private FileItem newJsFile(@RequestBody FileItem fileInfo, String projectId) {
        if (!fileInfo.getName().toLowerCase(Locale.ROOT).endsWith(".js")) {
            fileInfo.setName(fileInfo.getName() + ".js");
        }
        return saveJsFileInfo(fileInfo, projectId);
    }

    @SneakyThrows
    @PostMapping("/api")
    private FileItem newApiFile(@RequestBody FileItem fileInfo, String projectId) {
        if (!fileInfo.getName().toLowerCase(Locale.ROOT).endsWith(".mjs")) {
            fileInfo.setName(fileInfo.getName() + ".mjs");
        }
        fileInfo.setContent("""
                /**
                 * API接口文件 - <Enter your model name>
                 * @author <Enter your name>
                 * @date <Enter date>
                 */
                export default new ApiEndpoint({
                    model: '<Enter your model name>',
                    path: 'v1/chat/completions',
                    widget: 1,
                    service: (messages, option) => {\s
                
                    }
                })
                """);
        fileInfo.setContent(fileInfo.getContent().replace("<Enter date>", DateUtil.formatAll()));
        if (StringUtils.hasText(fileInfo.getModel())){
            fileInfo.setContent(fileInfo.getContent().replace("<Enter your model name>", fileInfo.getModel()));
        }
        if (!StringUtils.hasText(fileInfo.getUri())){
            fileInfo.setContent(fileInfo.getContent().replace("v1/chat/completions", fileInfo.getUri()));
        }
        return saveJsFileInfo(fileInfo, projectId);
    }

    /**
     * 保存JS文件详细信息
     * @param fileInfo
     *      文件信息
     * @return
     * @throws IOException
     */
    private FileItem saveJsFileInfo(@RequestBody FileItem fileInfo, String projectId) throws IOException {
        fileInfo.setKey(fileInfo.getKey() + "/" + fileInfo.getName());
        File file = new File(new File(Common.STORE_PATH, projectId), fileInfo.getKey());
        if (file.exists()) {
            throw new ValidationException("文件已存在");
        }
        if (!StringUtils.hasText(fileInfo.getContent())){
            fileInfo.setContent("export default {\n    \n}");
        }
        FileUtil.writeFile(fileInfo.getContent().getBytes(StandardCharsets.UTF_8), file);
        fileInfo.setType("file");
        if (fileInfo.getKey().contains("/apis/") && fileInfo.getName().endsWith(".mjs")) {
            String moduleName = fileInfo.getKey().substring(2).split("/apis/")[0];
            ModuleContainer.getEndpointContainer(moduleName).loadFile(file);
        }
        return fileInfo;
    }

    @SneakyThrows
    @PostMapping("/vue")
    public FileItem createVueFile(@RequestBody FileItem fileInfo, String projectId) {
        write(fileInfo, String.format("""
        <template>
        </template>
        
        <script>
        export default {
            name: '%s',
            data() {
                return {
                }
            },
            methods: {
            }
        }
        </script>
        <style lang="less" scoped>
        </style>
        """, fileInfo.getName().contains(".") ?
                fileInfo.getName().substring(0, fileInfo.getName().lastIndexOf(".")) :
                fileInfo.getName()), ".vue", projectId);
        return fileInfo;
    }

    @PostMapping("/model")
    public FileItem createModelFile(@RequestBody FileItem fileInfo, String projectId){
        if(fileInfo.getName().contains(".")){
            fileInfo.setName(fileInfo.getName().substring(0, fileInfo.getName().lastIndexOf(".")));
        }
        // 模型名称必须是大写开头的驼峰命名
        if(!fileInfo.getName().matches("^[A-Z][a-zA-Z]*$")){
            throw new ValidationException("模型名称必须是大写开头的纯字母驼峰命名");
        }
        write(fileInfo, String.format("""
        %s('table_name', '模型描述'){
            
        }
        """, fileInfo.getName().contains(".") ?
                fileInfo.getName().substring(0, fileInfo.getName().lastIndexOf(".")) :
                fileInfo.getName()), ".mds", projectId);
        return fileInfo;
    }

    @SneakyThrows
    private void write(FileItem fileInfo, String content, String suffix, String projectId) {
        if (!fileInfo.getName().toLowerCase(Locale.ROOT).endsWith(suffix)) {
            fileInfo.setName(fileInfo.getName() + suffix);
        }
        fileInfo.setKey(fileInfo.getKey() + "/" + fileInfo.getName());
        File file = new File(new File(Common.STORE_PATH, projectId), fileInfo.getKey());
        if (file.exists()) {
            throw new ValidationException("文件已存在");
        }
        // 写入文件
        FileUtil.writeFile(content.getBytes(StandardCharsets.UTF_8), file);
        fileInfo.setType("file");
    }
}
