package com.iflytek.cetsim.base.model;

import java.util.List;

/**
 * 通用的分页结果类
 *
 * Created by code2life on 2017/3/10.
 */
public class PageModel<T> {

    private Integer pageIndex;

    private Integer pageSize;

    private Integer total;

    private List<T> data;
    private int pages;

    public PageModel(int index,int size)
    {
        this.pageIndex = index;
        this.pageSize = size;
    }

    protected void Build()
    {
        this.pages = this.total % this.pageSize > 0 ? this.total / this.pageSize + 1: this.total / this.pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
        this.Build();
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
