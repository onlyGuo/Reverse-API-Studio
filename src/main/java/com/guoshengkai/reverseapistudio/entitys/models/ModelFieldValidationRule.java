package com.guoshengkai.reverseapistudio.entitys.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Copyright (c) 2021 HEBEI CLOUD IOT FACTORY BIGDATA CO.,LTD.
 * Legal liability shall be investigated for unauthorized use
 *
 * @Author: Guo Shengkai
 * @Date: Create in 2022/5/14 14:42
 */
@Getter
@Setter
public class ModelFieldValidationRule {

    private Double minValue;

    private Double maxValue;

    private Integer minLength;

    private Integer maxLength;

    @Override
    public String toString() {
        return "ModelFieldValidationRule{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", minLength=" + minLength +
                ", maxLength=" + maxLength +
                '}';
    }
}
