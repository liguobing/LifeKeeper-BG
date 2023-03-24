package com.lixyz.lifekeeper.bean.netdisk.image;

public class ImageCategoryCover {
    private ImageCategoryBean category;
    private ImageBean image;
    private int imageCount;

    public ImageCategoryBean getCategory() {
        return category;
    }

    public void setCategory(ImageCategoryBean category) {
        this.category = category;
    }

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }
}
