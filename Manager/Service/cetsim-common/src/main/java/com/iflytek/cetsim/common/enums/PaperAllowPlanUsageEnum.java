package com.iflytek.cetsim.common.enums;

/**
 * Created by pengwang on 2017/3/23.
 */
public enum PaperAllowPlanUsageEnum {
    ENABLED(true,"启用"),DISABLED(false,"禁用");

    private boolean allowPlanUsageCode;

    private String allowPlanUsageName;

    PaperAllowPlanUsageEnum(boolean allowPlanUsageCode, String allowPlanUsageName) {
        this.allowPlanUsageCode = allowPlanUsageCode;
        this.allowPlanUsageName = allowPlanUsageName;
    }
    public boolean getAllowPlanUsageCode() {
        return allowPlanUsageCode;
    }

    public String getAllowPlanUsageName() {
        return allowPlanUsageName;
    }
}
