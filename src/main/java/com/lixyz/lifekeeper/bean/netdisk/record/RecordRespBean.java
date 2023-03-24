package com.lixyz.lifekeeper.bean.netdisk.record;

import java.util.ArrayList;

public class RecordRespBean {
    private ContactBean contact;
    private ArrayList<RecordBean> records;
    private int recordCount;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public ContactBean getContact() {
        return contact;
    }

    public void setContact(ContactBean contact) {
        this.contact = contact;
    }

    public ArrayList<RecordBean> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<RecordBean> records) {
        this.records = records;
    }
}
