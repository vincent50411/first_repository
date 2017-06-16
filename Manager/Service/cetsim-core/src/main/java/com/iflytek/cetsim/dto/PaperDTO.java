package com.iflytek.cetsim.dto;

import com.iflytek.cetsim.model.PaperItem;

import java.util.List;

public class PaperDTO {

    private int paperId;

    private int paperType;

    private String paperPath;

    private String paperName;

    private List<PaperItem> paperDetail;

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public int getPaperType() {
        return paperType;
    }

    public void setPaperType(int paperType) {
        this.paperType = paperType;
    }

    public String getPaperPath() {
        return paperPath;
    }

    public void setPaperPath(String paperPath) {
        this.paperPath = paperPath;
    }

    public List<PaperItem> getPaperDetail() {
        return paperDetail;
    }

    public void setPaperDetail(List<PaperItem> paperDetail) {
        this.paperDetail = paperDetail;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }
}
