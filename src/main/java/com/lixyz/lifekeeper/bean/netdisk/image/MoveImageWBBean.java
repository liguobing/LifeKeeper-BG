package com.lixyz.lifekeeper.bean.netdisk.image;

import java.util.ArrayList;

public class MoveImageWBBean {
    private ArrayList<String> images;
    private String targetCategoryId;

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getTargetCategoryId() {
        return targetCategoryId;
    }

    public void setTargetCategoryId(String targetCategoryId) {
        this.targetCategoryId = targetCategoryId;
    }
}
