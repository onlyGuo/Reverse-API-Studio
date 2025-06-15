package com.guoshengkai.reverseapistudio.entitys.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Copyright (c) 2021 HEBEI CLOUD IOT FACTORY BIGDATA CO.,LTD.
 * Legal liability shall be investigated for unauthorized use
 *
 * @Author: Guo Shengkai
 * @Date: Create in 2022/5/14 14:34
 */
@Getter
@Setter
public class ModelEntity {

    private String name;

    private String tableName;

    private String description;

    private List<ModelField> fields;

    @Override
    public String toString() {
        return "ModelEntity{" +
                "name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", description='" + description + '\'' +
                ", fields=" + fields +
                '}';
    }

    public String toDropSql() {
        return "drop table if exists `" + tableName + "`";
    }

    public String toCreateSql() {
        StringBuilder sb = new StringBuilder("CREATE TABLE `" + tableName + "` (");
        for (ModelField field : fields) {
            if (StringUtils.hasLength(field.getTableFieldName())){
                sb.append(" `").append(field.getTableFieldName()).append("` ")
                        .append(FieldType.parse(field.getType()).getJdbcType()).append(" ")
                        .append(field.getDefaultValue() == null ? "null" : "DEFAULT '" + field.getDefaultValue() +  "'")
                        .append(" COMMENT '").append(field.getComment()).append("',");
            }
        }
        sb.append("_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键'");
        sb.append(");");
        return sb.toString();
    }
}
