package com.lixyz.lifekeeper.bean.netdisk.record;

import java.util.ArrayList;

public class WeChatRecordRespBean {
    private WeChatRecordContactBean contact;
    private ArrayList<WeChatRecordBean> records;
    private int recordCount;

    public WeChatRecordContactBean getContact() {
        return contact;
    }

    public void setContact(WeChatRecordContactBean contact) {
        this.contact = contact;
    }

    public ArrayList<WeChatRecordBean> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<WeChatRecordBean> records) {
        this.records = records;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
}
