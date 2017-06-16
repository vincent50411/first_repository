package com.iflytek.cetsim.dto;

import java.util.Date;

public class PaperDetail {
    private Integer id;

    private String name;

    private Integer type;

    private Integer createTeacher;

    private Date createTime;

    private Integer status;

    private double max_score;

    private double average_score;

    private int use_count;

    private int exam_count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCreateTeacher() {
        return createTeacher;
    }

    public void setCreateTeacher(Integer createTeacher) {
        this.createTeacher = createTeacher;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public double getMax_score() {
        return max_score;
    }

    public void setMax_score(double max_score) {
        this.max_score = max_score;
    }

    public double getAverage_score() {
        return average_score;
    }

    public void setAverage_score(double average_score) {
        this.average_score = average_score;
    }

    public int getUse_count() {
        return use_count;
    }

    public void setUse_count(int use_count) {
        this.use_count = use_count;
    }

    public int getExam_count() {
        return exam_count;
    }

    public void setExam_count(int exam_count) {
        this.exam_count = exam_count;
    }
}
