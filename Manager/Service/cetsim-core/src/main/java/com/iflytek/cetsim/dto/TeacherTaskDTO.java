package com.iflytek.cetsim.dto;

import java.util.Date;

public class TeacherTaskDTO {
    private int task_id;
    private int plan_id;
    private int student_id;
    private String plan_name;
    private String task_name;
    private String paper_name;
    private String paper_type;
    private Date start_time;
    private Date end_time;
    private String requirement;
    private int student_count;
    private int finish_count;
    private double max_score;
    private double min_score;
    private double average_score;
    private String finish_percent;
    private int status;
    /*-------------------jiehe2 add begin----------------------------------------*/
    private String plan_code;
    private String task_code;
    private String class_code;
    private String paper_code;
    /*-------------------jiehe2 add end----------------------------------------*/

    public String getPaper_code() {
        return paper_code;
    }

    public void setPaper_code(String paper_code) {
        this.paper_code = paper_code;
    }

    public String getTask_code() {
        return task_code;
    }

    public void setTask_code(String task_code) {
        this.task_code = task_code;
    }

    public String getClass_code() {
        return class_code;
    }

    public void setClass_code(String class_code) {
        this.class_code = class_code;
    }

    public String getPlan_code() {
        return plan_code;
    }

    public void setPlan_code(String plan_code) {
        this.plan_code = plan_code;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public int getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(int plan_id) {
        this.plan_id = plan_id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getPaper_name() {
        return paper_name;
    }

    public void setPaper_name(String paper_name) {
        this.paper_name = paper_name;
    }

    public String getPaper_type() {
        return paper_type;
    }

    public void setPaper_type(String paper_type) {
        this.paper_type = paper_type;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public int getStudent_count() {
        return student_count;
    }

    public void setStudent_count(int student_count) {
        this.student_count = student_count;
    }

    public double getMax_score() {
        return max_score;
    }

    public void setMax_score(double max_score) {
        this.max_score = max_score;
    }

    public double getMin_score() {
        return min_score;
    }

    public void setMin_score(double min_score) {
        this.min_score = min_score;
    }

    public String getFinish_percent() {
        return finish_percent;
    }

    public void setFinish_percent(String finish_percent) {
        this.finish_percent = finish_percent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public double getAverage_score() {
        return average_score;
    }

    public void setAverage_score(double average_score) {
        this.average_score = average_score;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public int getFinish_count() {
        return finish_count;
    }

    public void setFinish_count(int finish_count) {
        this.finish_count = finish_count;
    }
}
