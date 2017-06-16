package com.iflytek.cetsim.model;

public class PaperItemBuffer {
    private String code;

    private Integer id;

    private String paperItemCode;

    private String md5;

    private byte[] buffer;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaperItemCode() {
        return paperItemCode;
    }

    public void setPaperItemCode(String paperItemCode) {
        this.paperItemCode = paperItemCode == null ? null : paperItemCode.trim();
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5 == null ? null : md5.trim();
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }
}