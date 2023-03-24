package com.lixyz.lifekeeper.bean.netdisk.video;

public class VideoCategoryCover {
    private VideoCategoryBean category;
    private VideoBean video;
    private int videoCount;

    public VideoCategoryBean getCategory() {
        return category;
    }

    public void setCategory(VideoCategoryBean category) {
        this.category = category;
    }

    public VideoBean getVideo() {
        return video;
    }

    public void setVideo(VideoBean video) {
        this.video = video;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }
}

