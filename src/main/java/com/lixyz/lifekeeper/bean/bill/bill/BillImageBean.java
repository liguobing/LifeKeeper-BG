package com.lixyz.lifekeeper.bean.bill.bill;

public class BillImageBean {
    private String objectId;
    private String imageId;
    private String billId;
    private String imageSourceName;
    private String imageCoverName;
    private String imageThumbnailName;
    private String imageWebpName;
    private String imageUser;

    public BillImageBean() {
    }

    public BillImageBean(String objectId, String imageId, String billId, String imageSourceName, String imageCoverName, String imageThumbnailName, String imageWebpName, String imageUser) {
        this.objectId = objectId;
        this.imageId = imageId;
        this.billId = billId;
        this.imageSourceName = imageSourceName;
        this.imageCoverName = imageCoverName;
        this.imageThumbnailName = imageThumbnailName;
        this.imageWebpName = imageWebpName;
        this.imageUser = imageUser;
    }

    public String getImageWebpName() {
        return imageWebpName;
    }

    public void setImageWebpName(String imageWebpName) {
        this.imageWebpName = imageWebpName;
    }

    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getImageSourceName() {
        return imageSourceName;
    }

    public void setImageSourceName(String imageSourceName) {
        this.imageSourceName = imageSourceName;
    }

    public String getImageCoverName() {
        return imageCoverName;
    }

    public void setImageCoverName(String imageCoverName) {
        this.imageCoverName = imageCoverName;
    }

    public String getImageThumbnailName() {
        return imageThumbnailName;
    }

    public void setImageThumbnailName(String imageThumbnailName) {
        this.imageThumbnailName = imageThumbnailName;
    }
}
