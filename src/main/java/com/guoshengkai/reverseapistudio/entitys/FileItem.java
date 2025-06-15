package com.guoshengkai.reverseapistudio.entitys;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 文件项
 * @author gsk
 */
@Getter
@Setter
public class FileItem {

    private String name;

    private String key;

    /**
     * dir / file
     */
    private String type;

    private int level;

    private String content;

    private String model;

    private String uri;

    private List<FileItem> child;
}
