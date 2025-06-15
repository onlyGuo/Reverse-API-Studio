package com.guoshengkai.reverseapistudio.entitys.models;

import lombok.Getter;
import lombok.Setter;

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
public class ModelField {

    private String name;

    private boolean required;

    private String tableFieldName;

    private String type;

    private String comment;

    private String defaultValue;

    private List<ModelFieldValidationRule> validationRules;

    private String regex;

    @Override
    public String toString() {
        return "ModelField{" +
                "name='" + name + '\'' +
                ", tableFieldName='" + tableFieldName + '\'' +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", validationRules=" + validationRules +
                ", regex='" + regex + '\'' +
                '}';
    }

    /**
     * 生成指定表字段更新的SQL
     * @param tableName
     *      表名
     * @return
     */
    public String toUpdateSql(String tableName){
        return String.format(
                "ALTER TABLE `%s` MODIFY `%s` %s null COMMENT '%s'",
                tableName, getName(),FieldType.parse(getType()).getJdbcType(),
                getComment());
    }

    /**
     * 生成指定表字段添加的SQL
     * @param tableName
     *      表名
     * @return
     */
    public String toAddSql(String tableName){
        return String.format(
                "ALTER TABLE `%s` ADD `%s` %s null COMMENT '%s'",
                tableName, getName(),FieldType.parse(getType()).getJdbcType(),
                getComment());
    }

    /**
     * 生成指定表字段删除的SQL
     * @param tableName
     *      表名
     * @return
     */
    public String toDropSql(String tableName) {
        return String.format(
                "ALTER TABLE `%s` DROP COLUMN `%s`",
                tableName, getName());
    }
}
