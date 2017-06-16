package com.iflytek.cetsim.common.enums;

/**
 * Created by pengwang on 2017/3/23.
 */
public enum PaperAllowFreeUsageEnum {
    ENABLED(true,"启用"),DISABLED(false,"禁用");

    private boolean allowFreeUsageCode;

    private String allowFreeUsageName;

    PaperAllowFreeUsageEnum(boolean allowFreeUsageCode, String allowFreeUsageName) {
        this.allowFreeUsageCode = allowFreeUsageCode;
        this.allowFreeUsageName = allowFreeUsageName;
    }
    public boolean getAllowFreeUsageCode() {
        return allowFreeUsageCode;
    }

    public String getAllowFreeUsageName() {
        return allowFreeUsageName;
    }
}
