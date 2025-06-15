package com.guoshengkai.reverseapistudio.controllers;

import com.guoshengkai.reverseapistudio.Common;
import com.guoshengkai.reverseapistudio.entitys.FileItem;
import com.guoshengkai.reverseapistudio.exception.ValidationException;
import com.guoshengkai.reverseapistudio.utils.FileUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * 项目相关接口
 * @author gsk
 */
@RestController
@RequestMapping("api/v1/project")
public class ProjectController {

    @GetMapping("dir")
    public List<FileItem> getDirTree(String path, String projectId, Integer level){
        if (null == level){
            level = 0;
        }
        File file = new File(new File(Common.STORE_PATH, projectId), path);
        List<FileItem> fileItems = new ArrayList<>();
        if (!file.exists()) {
           newModule("DefaultModule", projectId);
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                // 排序，文件夹在前，按名称排序
                List<File> list = Arrays.asList(files);
                list.sort((f1, f2) -> {
                    if (f1.isDirectory() && !f2.isDirectory()) {
                        return -1; // 文件夹在前
                    } else if (!f1.isDirectory() && f2.isDirectory()) {
                        return 1; // 文件在后
                    } else {
                        return f1.getName().compareToIgnoreCase(f2.getName()); // 同时是文件或文件夹，按名称排序
                    }
                });
                for (File f : list) {
                    FileItem item = new FileItem();
                    item.setName(f.getName());
                    item.setKey(path + "/" + f.getName());
                    if (f.isDirectory()) {
                        item.setType("dir");
                        item.setChild(getDirTree(item.getKey(), projectId, level + 1));
                    }else{
                        item.setType("file");
                    }
                    item.setLevel(level);
                    fileItems.add(item);
                }
            }
        }else{
            throw new RuntimeException("路径不是一个目录: " + file.getAbsolutePath());
        }
        return fileItems;
    }

    /**
     * 读取文件内容
     * @param path
     *      文件路径
     * @param projectId
     *      项目ID
     * @return
     *      文件内容
     */
    @GetMapping("read")
    public String readFile(String path, String projectId){
        File file = new File(new File(Common.STORE_PATH, projectId), path);
        if (!file.exists()) {
            return "<UNK>: " + file.getAbsolutePath();
        }
        if (!file.isFile()) {
            return "<UNK>: " + file.getAbsolutePath() + " 不是一个文件";
        }
        return FileUtil.readFile(file);
    }

    @PostMapping("module/new")
    public void newModule(String moduleName, String projectId){
        // 创建模块目录
        if (!StringUtils.hasLength(moduleName)){
            throw new ValidationException("模块名称不能为空");
        }
        // 正则判断是否由大小写字母组成
        if(!Pattern.matches("([a-z]|[A-Z])+", moduleName)){
            throw new ValidationException("模块名称必须由英文字母组成");
        }
        File file =  new File(new File(Common.STORE_PATH, projectId), moduleName);
        if (file.exists()){
            throw new ValidationException("该模块已存在");
        }
        file.mkdirs();
        new File(file, "apis").mkdirs();
        new File(file, "assets").mkdirs();
        new File(file, "models").mkdirs();
        new File(file, "views").mkdirs();

        FileUtil.writeFile("""
                export default {
                    name: '%s',
                    description: '',
                    version: '1.0.0',
                    routers: [
                    
                    ]
                }
                """.formatted(moduleName).getBytes(StandardCharsets.UTF_8), new File(file, "config.js"));

        FileUtil.writeFile("""
               # 目录说明
               ```
               - modules
                   |- apis         // 存放对外发布的API以及各种业务和逻辑
                   |- assets       // 存放静态文件(在本项目中暂时弃用)
                   |- models       // 存放业务模型文件(在本项目中暂时弃用)
                   |- views        // 存放业务文件(在本项目中暂时弃用)
               ```

               ## API文件说明
               每个API文件要有`model`属性，表示要响应的模型请求。例如`qwen3`、`gpt-4o`等。
               同时，每个API文件有可选属性`path`, 表示要响应的请求路径。这个字段是一个可选字段，默认为`v1/chat/completions`.
               另外,`widget`字段表示接口权重，默认为`1`，权重越高，当拥有两个同地址同模型的接口时，匹配到的概率就越高。当权重为`0`时，表示该接口不允许外部访问
               ```javascript
               export default {
                   model: 'qwen3:32b',             // 要响应的模型
                   path: 'v1/chat/completions',    // 要响应的请求地址
                   widget: 10,                     // 接口权重
                   service(Message[] messages, Option option) {\s

                   }
               }
               ```
               """.getBytes(StandardCharsets.UTF_8), new File(file, "README.md"));

        file.setWritable(true);
        file.setReadable(true);
        file.setLastModified(System.currentTimeMillis());
    }
}
