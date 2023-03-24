package com.lixyz.lifekeeper.bean.netdisk.image;

import java.util.ArrayList;

public class ImageForGroupBean {
    private ArrayList<String> groupList;
    private ArrayList<ArrayList<ImageBean>> itemList;

    public ArrayList<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<String> groupList) {
        this.groupList = groupList;
    }

    public ArrayList<ArrayList<ImageBean>> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<ArrayList<ImageBean>> itemList) {
        this.itemList = itemList;
    }

}
