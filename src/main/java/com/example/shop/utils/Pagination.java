package com.example.shop.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库的分页结果，
 */
@Getter
@Setter
public class Pagination<T> implements Serializable {
    private final List<T> items;
    private final int pageSize;
    private final int pageNum;
    private final int totalPage;
    private final String status;
    private final String msg;

    private Pagination(List<T> items, int pageSize, int pageNum, int totalPage, boolean success) {
        this.items = items;
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

    @Override
    public String toString() {
        return "Pagination{" +
                "items=" + items +
                ", pageSize=" + pageSize +
                ", pageNum=" + pageNum +
                ", totalPage=" + totalPage +
                ", status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
