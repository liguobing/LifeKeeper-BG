package com.lixyz.lifekeeper.bean.netdisk.image;

import java.util.ArrayList;

public class ImageThumbnailBean {
    private ArrayList<String> titleList;
    private ArrayList<ArrayList<ImageBean>> imageList;

    public ArrayList<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(ArrayList<String> titleList) {
        this.titleList = titleList;
    }

    public ArrayList<ArrayList<ImageBean>> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ArrayList<ImageBean>> imageList) {
        this.imageList = imageList;
    }
}
