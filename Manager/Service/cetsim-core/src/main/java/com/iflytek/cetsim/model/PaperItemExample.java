package com.iflytek.cetsim.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaperItemExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public PaperItemExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andCodeIsNull() {
            addCriterion("code is null");
            return (Criteria) this;
        }

        public Criteria andCodeIsNotNull() {
            addCriterion("code is not null");
            return (Criteria) this;
        }

        public Criteria andCodeEqualTo(String value) {
            addCriterion("code =", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotEqualTo(String value) {
            addCriterion("code <>", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThan(String value) {
            addCriterion("code >", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThanOrEqualTo(String value) {
            addCriterion("code >=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThan(String value) {
            addCriterion("code <", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThanOrEqualTo(String value) {
            addCriterion("code <=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLike(String value) {
            addCriterion("code like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotLike(String value) {
            addCriterion("code not like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeIn(List<String> values) {
            addCriterion("code in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotIn(List<String> values) {
            addCriterion("code not in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeBetween(String value1, String value2) {
            addCriterion("code between", value1, value2, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotBetween(String value1, String value2) {
            addCriterion("code not between", value1, value2, "code");
            return (Criteria) this;
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("`name` is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("`name` is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("`name` =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("`name` <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("`name` >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("`name` >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("`name` <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("`name` <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("`name` like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("`name` not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("`name` in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("`name` not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("`name` between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("`name` not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeIsNull() {
            addCriterion("paper_item_type_code is null");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeIsNotNull() {
            addCriterion("paper_item_type_code is not null");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeEqualTo(String value) {
            addCriterion("paper_item_type_code =", value, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeNotEqualTo(String value) {
            addCriterion("paper_item_type_code <>", value, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeGreaterThan(String value) {
            addCriterion("paper_item_type_code >", value, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeGreaterThanOrEqualTo(String value) {
            addCriterion("paper_item_type_code >=", value, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeLessThan(String value) {
            addCriterion("paper_item_type_code <", value, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeLessThanOrEqualTo(String value) {
            addCriterion("paper_item_type_code <=", value, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeLike(String value) {
            addCriterion("paper_item_type_code like", value, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeNotLike(String value) {
            addCriterion("paper_item_type_code not like", value, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeIn(List<String> values) {
            addCriterion("paper_item_type_code in", values, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeNotIn(List<String> values) {
            addCriterion("paper_item_type_code not in", values, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeBetween(String value1, String value2) {
            addCriterion("paper_item_type_code between", value1, value2, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andPaperItemTypeCodeNotBetween(String value1, String value2) {
            addCriterion("paper_item_type_code not between", value1, value2, "paperItemTypeCode");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("`status` is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("`status` is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Short value) {
            addCriterion("`status` =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Short value) {
            addCriterion("`status` <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Short value) {
            addCriterion("`status` >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Short value) {
            addCriterion("`status` >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Short value) {
            addCriterion("`status` <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Short value) {
            addCriterion("`status` <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Short> values) {
            addCriterion("`status` in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Short> values) {
            addCriterion("`status` not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Short value1, Short value2) {
            addCriterion("`status` between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Short value1, Short value2) {
            addCriterion("`status` not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageIsNull() {
            addCriterion("allow_plan_useage is null");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageIsNotNull() {
            addCriterion("allow_plan_useage is not null");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageEqualTo(Boolean value) {
            addCriterion("allow_plan_useage =", value, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageNotEqualTo(Boolean value) {
            addCriterion("allow_plan_useage <>", value, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageGreaterThan(Boolean value) {
            addCriterion("allow_plan_useage >", value, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageGreaterThanOrEqualTo(Boolean value) {
            addCriterion("allow_plan_useage >=", value, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageLessThan(Boolean value) {
            addCriterion("allow_plan_useage <", value, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageLessThanOrEqualTo(Boolean value) {
            addCriterion("allow_plan_useage <=", value, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageIn(List<Boolean> values) {
            addCriterion("allow_plan_useage in", values, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageNotIn(List<Boolean> values) {
            addCriterion("allow_plan_useage not in", values, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageBetween(Boolean value1, Boolean value2) {
            addCriterion("allow_plan_useage between", value1, value2, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowPlanUseageNotBetween(Boolean value1, Boolean value2) {
            addCriterion("allow_plan_useage not between", value1, value2, "allowPlanUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageIsNull() {
            addCriterion("allow_free_useage is null");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageIsNotNull() {
            addCriterion("allow_free_useage is not null");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageEqualTo(Boolean value) {
            addCriterion("allow_free_useage =", value, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageNotEqualTo(Boolean value) {
            addCriterion("allow_free_useage <>", value, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageGreaterThan(Boolean value) {
            addCriterion("allow_free_useage >", value, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageGreaterThanOrEqualTo(Boolean value) {
            addCriterion("allow_free_useage >=", value, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageLessThan(Boolean value) {
            addCriterion("allow_free_useage <", value, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageLessThanOrEqualTo(Boolean value) {
            addCriterion("allow_free_useage <=", value, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageIn(List<Boolean> values) {
            addCriterion("allow_free_useage in", values, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageNotIn(List<Boolean> values) {
            addCriterion("allow_free_useage not in", values, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageBetween(Boolean value1, Boolean value2) {
            addCriterion("allow_free_useage between", value1, value2, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andAllowFreeUseageNotBetween(Boolean value1, Boolean value2) {
            addCriterion("allow_free_useage not between", value1, value2, "allowFreeUseage");
            return (Criteria) this;
        }

        public Criteria andSummaryIsNull() {
            addCriterion("summary is null");
            return (Criteria) this;
        }

        public Criteria andSummaryIsNotNull() {
            addCriterion("summary is not null");
            return (Criteria) this;
        }

        public Criteria andSummaryEqualTo(String value) {
            addCriterion("summary =", value, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryNotEqualTo(String value) {
            addCriterion("summary <>", value, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryGreaterThan(String value) {
            addCriterion("summary >", value, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryGreaterThanOrEqualTo(String value) {
            addCriterion("summary >=", value, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryLessThan(String value) {
            addCriterion("summary <", value, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryLessThanOrEqualTo(String value) {
            addCriterion("summary <=", value, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryLike(String value) {
            addCriterion("summary like", value, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryNotLike(String value) {
            addCriterion("summary not like", value, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryIn(List<String> values) {
            addCriterion("summary in", values, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryNotIn(List<String> values) {
            addCriterion("summary not in", values, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryBetween(String value1, String value2) {
            addCriterion("summary between", value1, value2, "summary");
            return (Criteria) this;
        }

        public Criteria andSummaryNotBetween(String value1, String value2) {
            addCriterion("summary not between", value1, value2, "summary");
            return (Criteria) this;
        }

        public Criteria andDifficultyIsNull() {
            addCriterion("difficulty is null");
            return (Criteria) this;
        }

        public Criteria andDifficultyIsNotNull() {
            addCriterion("difficulty is not null");
            return (Criteria) this;
        }

        public Criteria andDifficultyEqualTo(Float value) {
            addCriterion("difficulty =", value, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyNotEqualTo(Float value) {
            addCriterion("difficulty <>", value, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyGreaterThan(Float value) {
            addCriterion("difficulty >", value, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyGreaterThanOrEqualTo(Float value) {
            addCriterion("difficulty >=", value, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyLessThan(Float value) {
            addCriterion("difficulty <", value, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyLessThanOrEqualTo(Float value) {
            addCriterion("difficulty <=", value, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyIn(List<Float> values) {
            addCriterion("difficulty in", values, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyNotIn(List<Float> values) {
            addCriterion("difficulty not in", values, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyBetween(Float value1, Float value2) {
            addCriterion("difficulty between", value1, value2, "difficulty");
            return (Criteria) this;
        }

        public Criteria andDifficultyNotBetween(Float value1, Float value2) {
            addCriterion("difficulty not between", value1, value2, "difficulty");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andKnowledgeIsNull() {
            addCriterion("knowledge is null");
            return (Criteria) this;
        }

        public Criteria andKnowledgeIsNotNull() {
            addCriterion("knowledge is not null");
            return (Criteria) this;
        }

        public Criteria andKnowledgeEqualTo(String value) {
            addCriterion("knowledge =", value, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeNotEqualTo(String value) {
            addCriterion("knowledge <>", value, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeGreaterThan(String value) {
            addCriterion("knowledge >", value, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeGreaterThanOrEqualTo(String value) {
            addCriterion("knowledge >=", value, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeLessThan(String value) {
            addCriterion("knowledge <", value, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeLessThanOrEqualTo(String value) {
            addCriterion("knowledge <=", value, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeLike(String value) {
            addCriterion("knowledge like", value, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeNotLike(String value) {
            addCriterion("knowledge not like", value, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeIn(List<String> values) {
            addCriterion("knowledge in", values, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeNotIn(List<String> values) {
            addCriterion("knowledge not in", values, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeBetween(String value1, String value2) {
            addCriterion("knowledge between", value1, value2, "knowledge");
            return (Criteria) this;
        }

        public Criteria andKnowledgeNotBetween(String value1, String value2) {
            addCriterion("knowledge not between", value1, value2, "knowledge");
            return (Criteria) this;
        }

        public Criteria andReserved1IsNull() {
            addCriterion("reserved1 is null");
            return (Criteria) this;
        }

        public Criteria andReserved1IsNotNull() {
            addCriterion("reserved1 is not null");
            return (Criteria) this;
        }

        public Criteria andReserved1EqualTo(String value) {
            addCriterion("reserved1 =", value, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1NotEqualTo(String value) {
            addCriterion("reserved1 <>", value, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1GreaterThan(String value) {
            addCriterion("reserved1 >", value, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1GreaterThanOrEqualTo(String value) {
            addCriterion("reserved1 >=", value, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1LessThan(String value) {
            addCriterion("reserved1 <", value, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1LessThanOrEqualTo(String value) {
            addCriterion("reserved1 <=", value, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1Like(String value) {
            addCriterion("reserved1 like", value, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1NotLike(String value) {
            addCriterion("reserved1 not like", value, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1In(List<String> values) {
            addCriterion("reserved1 in", values, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1NotIn(List<String> values) {
            addCriterion("reserved1 not in", values, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1Between(String value1, String value2) {
            addCriterion("reserved1 between", value1, value2, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved1NotBetween(String value1, String value2) {
            addCriterion("reserved1 not between", value1, value2, "reserved1");
            return (Criteria) this;
        }

        public Criteria andReserved2IsNull() {
            addCriterion("reserved2 is null");
            return (Criteria) this;
        }

        public Criteria andReserved2IsNotNull() {
            addCriterion("reserved2 is not null");
            return (Criteria) this;
        }

        public Criteria andReserved2EqualTo(String value) {
            addCriterion("reserved2 =", value, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2NotEqualTo(String value) {
            addCriterion("reserved2 <>", value, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2GreaterThan(String value) {
            addCriterion("reserved2 >", value, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2GreaterThanOrEqualTo(String value) {
            addCriterion("reserved2 >=", value, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2LessThan(String value) {
            addCriterion("reserved2 <", value, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2LessThanOrEqualTo(String value) {
            addCriterion("reserved2 <=", value, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2Like(String value) {
            addCriterion("reserved2 like", value, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2NotLike(String value) {
            addCriterion("reserved2 not like", value, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2In(List<String> values) {
            addCriterion("reserved2 in", values, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2NotIn(List<String> values) {
            addCriterion("reserved2 not in", values, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2Between(String value1, String value2) {
            addCriterion("reserved2 between", value1, value2, "reserved2");
            return (Criteria) this;
        }

        public Criteria andReserved2NotBetween(String value1, String value2) {
            addCriterion("reserved2 not between", value1, value2, "reserved2");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}