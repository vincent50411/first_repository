package com.iflytek.cetsim.dto;

public class UpgradeDTO {
    private String ver;
    private String product;
    private String pkg;
    private String miniVer;
    private String info;
    private Integer size;
    private boolean force;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getMiniVer() {
        return miniVer;
    }

    public void setMiniVer(String miniVer) {
        this.miniVer = miniVer;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
