package com.lixyz.lifekeeper.bean.netdisk.image;

import java.util.ArrayList;

public class PageImageBean {
    //分类名称
    private String categoryName;
    //当前页
    private int currentPage;
    //每页的数量
    private int pageSize;
    //总记录数
    private long ImageCount;
    //总页数
    private int pageCount;
    //结果集
    private ArrayList<ImageBean> images;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getImageCount() {
        return ImageCount;
    }

    public void setImageCount(long imageCount) {
        ImageCount = imageCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public ArrayList<ImageBean> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageBean> images) {
        this.images = images;
    }
}
