package com.dxd.community.entity;

/**
 * @author dxd
 * @create 2021-06-18 17:33
 */

public class Page {
    private int current = 1;
    private int limit = 1;
    private int rows;
    private String path;

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <=100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //获取当前页的起始行
    public int getOffset(){
        return (current - 1) * limit;
    }

    public int getTotal(){
        return rows % limit ==0 ? rows/limit : (rows/limit) + 1;
    }

    //获取终止页
    public int getTo(){
        int to = current + 2;
        return to > getTotal() ? getTotal(): to;
    }

    //获取起始页
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1: from;
    }
}
