package com.iflytek.cetsim.common.enums;

public enum RoleEnum {

    Admin(0,"管理员"), Teacher(1,"教师"), Student(2,"学生");

    private int roleCode;

    private String roleName;

    RoleEnum(int roleCode, String roleName) {
        this.roleCode = roleCode;
        this.roleName = roleName;
    }

    public int getRoleCode() {
        return roleCode;
    }

    public String getRoleName() {
        return roleName;
    }
}
