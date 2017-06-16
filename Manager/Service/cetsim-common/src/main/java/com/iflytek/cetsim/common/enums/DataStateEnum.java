package com.iflytek.cetsim.common.enums;

public enum DataStateEnum {
    NO_UPLOAD(0,"未上传"),
    UPLOAD_SUCCESS(1,"上传成功"),
    UPLOAD_FAIL(2,"上传失败");

    private int code;

    private String name;

    DataStateEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
