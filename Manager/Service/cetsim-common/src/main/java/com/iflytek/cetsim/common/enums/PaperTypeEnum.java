package com.iflytek.cetsim.common.enums;

/**
 * 试卷类型枚举，code统一大写
 * Created by pengwang on 2017/3/23.
 */
public enum PaperTypeEnum {
    CET4("CET4","四级"),CET6("CET6","六级");

    private String typeCode;

    private String typeName;

    PaperTypeEnum(String typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }
    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
    }
}
