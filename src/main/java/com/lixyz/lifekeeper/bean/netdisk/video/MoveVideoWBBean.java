package com.lixyz.lifekeeper.bean.netdisk.video;

import java.util.ArrayList;

public class MoveVideoWBBean {
    private ArrayList<String> videos;
    private String targetCategoryId;

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    public String getTargetCategoryId() {
        return targetCategoryId;
    }

    public void setTargetCategoryId(String targetCategoryId) {
        this.targetCategoryId = targetCategoryId;
    }
}
