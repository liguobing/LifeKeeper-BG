package com.lixyz.lifekeeper.bean.netdisk.video;

import java.util.ArrayList;

public class PageVideoBean {
    //分类名称
    private String categoryName;
    //当前页
    private int currentPage;
    //每页的数量
    private int pageSize;
    //总记录数
    private long videoCount;
    //总页数
    private int pageCount;
    //结果集
    private ArrayList<VideoBean> videos;

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

    public long getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(long videoCount) {
        this.videoCount = videoCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public ArrayList<VideoBean> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<VideoBean> videos) {
        this.videos = videos;
    }
}
