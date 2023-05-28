package com.lixyz.lifekeeper.bean.netdisk;

public class NetDiskOverview {
    private int imageCount;
    private int recordCount;
    private int videoCount;
    private int weChatRecordCount;

    public int getWeChatRecordCount() {
        return weChatRecordCount;
    }

    public void setWeChatRecordCount(int weChatRecordCount) {
        this.weChatRecordCount = weChatRecordCount;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }
}
