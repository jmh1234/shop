package com.example.shop.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Pagination<T> implements Serializable {
    private final List<T> data;
    private final int pageSize;
    private final int pageNum;
    private final int totalPage;
    private final String status;
    private final String msg;

    private Pagination(List<T> data, int pageSize, int pageNum, int totalPage, boolean success) {
        this.data = data;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.totalPage = totalPage;
        this.status = success ? "ok" : "fail";
        this.msg = success ? "获取成功" : "系统异常";
    }

    public static <T> Pagination<T> pageOf(
            List<T> items, int pageSize, int pageNum, int totalPage, boolean success) {
        return new Pagination<>(items, pageSize, pageNum, totalPage, success);
    }
}
