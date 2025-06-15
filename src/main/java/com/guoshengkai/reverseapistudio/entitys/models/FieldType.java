package com.guoshengkai.reverseapistudio.entitys.models;

import java.util.HashMap;
import java.util.Map;

public enum FieldType {

    /**
     * 字段类型
     */
    STRING("string", "VARCHAR(255)"),
    INT("int", "int(11)"),
    LONG("long", "bigint(20)"),
    DOUBLE("double", "bigdecimal(20,2)"),
    BOOLEAN("boolean", "int(1)"),
    OBJECT("object", "longtext"),
    DATA_TIME("datetime", "datetime"),
    TIME("time", "timestamp"),
    LONG_TEXT("longtext", "longtext");

    private String scriptType;
    private String jdbcType;
    FieldType(String scriptType, String jdbcType) {
        this.scriptType = scriptType;
        this.jdbcType = jdbcType;
    }
    public String getScriptType() {
        return scriptType;
    }
    public String getJdbcType() {
        return jdbcType;
    }

    private static final Map<String, FieldType> SCRIPT_TYPES = new HashMap<>();
    static {
        for (FieldType fieldType : FieldType.values()) {
            SCRIPT_TYPES.put(fieldType.getScriptType(), fieldType);
        }
    }

    public static FieldType parse(String scriptType) {
        return SCRIPT_TYPES.get(scriptType);
    }
}
