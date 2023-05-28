package com.lixyz.lifekeeper.service;

import cn.hutool.core.date.DateUtil;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.gson.Gson;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.netdisk.image.*;
import com.lixyz.lifekeeper.bean.user.UserBean;
import com.lixyz.lifekeeper.dao.ImageDao;
import com.lixyz.lifekeeper.util.FileUtil;
import com.lixyz.lifekeeper.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ImageService {
    private final ImageDao imageDao;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private final SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.CHINA);
    private String pattern = "[1-9]{1}[0-9]{3}:[0-9]{2}:[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";

    public ImageService(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    /**
     * 上传照片
     */
    @Transactional(rollbackFor = Exception.class)
    public Result uploadImage(MultipartFile mSourceFile, String image) {
        try {
            //文件是否为空
            if (mSourceFile.isEmpty()) {
                return new Result(false, "文件为空", null, null);
            }
            ImageBean imageBean = new Gson().fromJson(image, ImageBean.class);
            //检查照片是否已经存在
            File tmpFile = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/tmp/" + imageBean.getSourceFileName());
            FileUtil.multipartFileTransferToFile(mSourceFile, tmpFile);
            String sha1 = FileUtil.getFileSha1(tmpFile);
            long count = imageDao.queryImageSha1(sha1, imageBean.getImageUser());
            tmpFile.delete();
            if (count > 0) {
                return new Result(true, null, null, 1);
            } else {
                imageBean.setSha1(sha1);
            }
            //检查目录是否存在，不存在就创建
            File dir = new File("/files/LifeKeeperImage/" + imageBean.getImageUser());
            if (!dir.exists()) {
                boolean mkdir = dir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            File sourceDir = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/source");
            if (!sourceDir.exists()) {
                boolean mkdir = sourceDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            File thumbnailDir = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/thumbnail");
            if (!thumbnailDir.exists()) {
                boolean mkdir = thumbnailDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            File coverDir = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/cover");
            if (!coverDir.exists()) {
                boolean mkdir = coverDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            File blurDir = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/blur");
            if (!blurDir.exists()) {
                boolean mkdir = blurDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            //查看是PC端还是移动端上传的（PC端ImageCategory有值，移动端为空）
            if (imageBean.getFileCategory() == null) {
                //如果是移动端上传的，就先将所有图片上传的“未分类”相册
                //先获取到“未分类”相册
                ImageCategoryBean category = imageDao.getDefaultCategory(imageBean.getImageUser());
                //如果该用户没有“未分类”相册，则创建之
                if (category == null) {
                    ImageCategoryBean categoryBean = new ImageCategoryBean();
                    categoryBean.setObjectId(StringUtil.getRandomString());
                    categoryBean.setCategoryId(StringUtil.getRandomString());
                    categoryBean.setCategoryName("未分类");
                    categoryBean.setCategoryUser(imageBean.getImageUser());
                    categoryBean.setIsPrivate(-1);
                    categoryBean.setPassword(StringUtil.string2MD5("", ""));
                    categoryBean.setCategoryStatus(1);
                    categoryBean.setCategoryType(0);
                    categoryBean.setCreateTime(System.currentTimeMillis());
                    categoryBean.setUpdateTime(0);
                    long l = imageDao.addImageCategory(categoryBean);
                    if (l > 0) {
                        imageBean.setFileCategory(categoryBean.getCategoryId());
                    } else {
                        return new Result(false, "添加默认分类出错", null, null);
                    }
                } else {
                    imageBean.setFileCategory(category.getCategoryId());
                }
            }
            //保存原始图片
            File dest = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/source/" + imageBean.getSourceFileName());
            FileUtil.multipartFileTransferToFile(mSourceFile, dest);
            //获取元数据，设置年月
            long longTime = 0;
            Metadata metadata = ImageMetadataReader.readMetadata(dest);
            for (Directory exif : metadata.getDirectories()) {
                for (Tag tag : exif.getTags()) {
                    if ("Date/Time Original".equals(tag.getTagName())) {
                        String description = tag.getDescription();
                        if (description.contains("GMT")) {//GMT 时间
                            TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
                            gmtFormat.setTimeZone(tz);
                            Date gmtDate = gmtFormat.parse(description);
                            longTime = gmtDate.getTime();
                        } else if (description.trim().length() == 0) {//Exif 时间为空
                            longTime = System.currentTimeMillis();
                        } else if (Pattern.matches(pattern, description)) {//正常的 xxxx:xx:xx xx:xx:xx时间格式
                            TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
                            format.setTimeZone(tz);
                            Date parse = format.parse(description);
                            longTime = parse.getTime();
                        } else {//其他格式
                            longTime = System.currentTimeMillis();
                        }
                    }
                }
            }
            //没有 Exif 时间字段
            if (longTime == 0) {
                longTime = System.currentTimeMillis();
            }
            imageBean.setMetaTime(longTime);
            int year = DateUtil.year(new Date(longTime));
            int month = DateUtil.month(new Date(longTime)) + 1;
            String str = month > 9 ? month + "" : "0" + month;

            imageBean.setYearMonth(year + "-" + str);

            //保存 Cover
            File coverFile = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/cover/" + imageBean.getSourceFileName());
            FileUtil.getCoverFile(dest, coverFile);
            //保存 Thumbnail
            File thumbnailFile = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/thumbnail/" + imageBean.getSourceFileName());
            FileUtil.getThumbnailFile(coverFile, thumbnailFile);
            //保存 Blur
            File blurFile = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/blur/" + imageBean.getSourceFileName());
            FileUtil.getBlurFile(thumbnailFile, blurFile);

            File thumbnailWebp = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/thumbnail/" + imageBean.getThumbnailFileName());
            if (thumbnailFile.exists()) {
                BufferedImage bufferedImage = ImageIO.read(thumbnailFile);
                boolean webp = ImageIO.write(bufferedImage, "webp", thumbnailWebp);
                if (webp) {
                    System.out.println("webp thumbnail create success");
                    boolean delete = thumbnailFile.delete();
                }
            }
            File coverWebp = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/cover/" + imageBean.getCoverFileName());
            if (coverFile.exists()) {
                BufferedImage bufferedImage = ImageIO.read(coverFile);
                boolean webp = ImageIO.write(bufferedImage, "webp", coverWebp);
                if (webp) {
                    System.out.println("webp cover create success");
                    boolean delete = coverFile.delete();
                }
            }
            File blurWebp = new File("/files/LifeKeeperImage/" + imageBean.getImageUser() + "/blur/" + imageBean.getBlurFileName());
            if (blurFile.exists()) {
                BufferedImage bufferedImage = ImageIO.read(blurFile);
                boolean webp = ImageIO.write(bufferedImage, "webp", blurWebp);
                if (webp) {
                    System.out.println("webp blur create success");
                    boolean delete = blurFile.delete();
                }
            }
            //插入数据库
            long insertResult = imageDao.insertImage(
                    imageBean.getObjectId(),
                    imageBean.getImageId(),
                    imageBean.getSha1(),
                    imageBean.getFileCategory(),
                    imageBean.getOriginalFileName(),
                    imageBean.getSourceFileName(),
                    imageBean.getThumbnailFileName(),
                    imageBean.getCoverFileName(),
                    imageBean.getBlurFileName(),
                    imageBean.getCreateTime(),
                    imageBean.getImageUser(),
                    imageBean.getImageStatus(),
                    imageBean.getImageType(),
                    imageBean.getUpdateTime(),
                    imageBean.getMetaTime(),
                    imageBean.getYearMonth());
            if (insertResult > 0) {
                return new Result(true, null, null, imageDao.getImageCountByUserId(imageBean.getImageUser()));
            } else {
                return new Result(false, "数据库插入数据出错", null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "数据库出错", null, null);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "保存文件出错", null, null);
        } catch (ImageProcessingException e) {
            e.printStackTrace();
            return new Result(false, "获取拍照日期出错", null, null);
        } catch (ParseException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new Result(false, "转化拍照日期出错", null, null);
        }
    }

    public long getIMageOverview(String userId) {
        try {
            return imageDao.queryImageCountByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result deleteImages(ArrayList<String> objectIds) {
        try {
            int count = imageDao.deleteImageByObjectId(objectIds);
            if (count == objectIds.size()) {
                return new Result(true, null, null, null);
            } else {
                return new Result(false, null, null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "数据库出错", e, null);
        }

    }

    public Result addImageCategory(String category) {
        try {
            ImageCategoryBean bean = new Gson().fromJson(category, ImageCategoryBean.class);
            ImageCategoryBean i = imageDao.categoryNameIsExists(bean.getCategoryName(), bean.getCategoryUser());
            if (i != null) {
                return new Result(true, null, null, bean.getCategoryId());
            } else {
                long l = imageDao.addImageCategory(bean);
                if (l > 0) {
                    return new Result(true, null, null, bean.getCategoryId());
                } else {
                    return new Result(false, "添加相册失败", null, null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "添加相册失败", null, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result updateCategory(String bean) {
        try {
            ImageCategoryBean imageCategory = new Gson().fromJson(bean, ImageCategoryBean.class);
            int count = imageDao.updateImageCategory(imageCategory);
            return new Result(count > 0, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result deleteImageCategory(String userId, String categoryId) {
        try {
            int i = imageDao.deleteImageCategory(userId, categoryId);
            return new Result(i > 0, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result checkImageCategoryPassword(String userId, String categoryId, String password) {
        try {
            int count = imageDao.checkImageCategoryPassword(userId, categoryId, password);
            return new Result(count > 0, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getImageCategoryAndCover(String userId) {
        try {
            ArrayList<ImageCategoryBean> imageCategories = imageDao.getImageCategories(userId);
            ArrayList<ImageCategoryCover> list = new ArrayList<>();
            for (ImageCategoryBean bean : imageCategories) {
                ImageBean imageBean = imageDao.getLatestImageByCategory(bean.getCategoryId());
                if (imageBean != null) {
                    ImageCategoryCover cover = new ImageCategoryCover();
                    cover.setCategory(bean);
                    cover.setImage(imageBean);
                    cover.setImageCount(imageDao.getImageCountByCategory(bean.getCategoryId(), userId));
                    list.add(cover);
                }
            }
            return new Result(true, null, null, list);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "出错啦", e, null);
        }
    }

    public Result getOtherImageCategory(String userId, String categoryId) {
        try {
            ArrayList<ImageCategoryBean> categoryList = imageDao.getOtherCategory(categoryId, userId);
            return new Result(true, null, null, categoryList);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result moveImages(MoveImageWBBean bean, String userId) {
        try {
            String targetCategoryId = bean.getTargetCategoryId();
            ArrayList<String> imageObjectIds = bean.getImages();
            int count = imageDao.moveImages(targetCategoryId, imageObjectIds, userId, System.currentTimeMillis());
            if (count == bean.getImages().size()) {
                return new Result(true, null, null, true);
            } else {
                return new Result(false, "服务器出错，请稍候重试", null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result getRangeImagesByCategory(String categoryId, String userId, int offset, int rows) {
        try {
            if (rows == -1) {
                ArrayList<ImageBean> images = imageDao.getRangeImageByCategory(categoryId, userId, 0, Integer.MAX_VALUE);
                PageImageBean bean = new PageImageBean();
                bean.setCategoryName("");
                bean.setImageCount(0);
                bean.setImages(images);
                bean.setCurrentPage(0);
                bean.setPageCount(0);
                bean.setPageSize(0);
                return new Result(true, null, null, bean);
            } else {
                ArrayList<ImageBean> images = imageDao.getRangeImageByCategory(categoryId, userId, (offset - 1) * rows, rows);
                String categoryName = imageDao.getImageCategoryName(categoryId, userId);
                int videoCount = imageDao.getImageCountByCategory(categoryId, userId);
                int pageCount;
                if (videoCount % rows == 0) {
                    pageCount = videoCount / rows;
                } else {
                    pageCount = (videoCount / rows) + 1;
                }
                PageImageBean bean = new PageImageBean();
                bean.setCategoryName(categoryName);
                bean.setImageCount(videoCount);
                bean.setImages(images);
                bean.setCurrentPage(offset);
                bean.setPageCount(pageCount);
                bean.setPageSize(rows);
                return new Result(true, null, null, bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result verifyAccountPassword(String userId, String password) {
        try {
            UserBean userBean = imageDao.getUserByUserId(userId);
            String phone = userBean.getUserPhone();
            return new Result(StringUtil.string2MD5(phone, password).equals(userBean.getUserPassword()), null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result verifyImageCategoryPassword(String userId, String categoryId, String password) {
        try {
            int count = imageDao.verifyImageCategoryPassword(userId, categoryId, password);
            return new Result(count > 0, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getImageCount(String userId) {
        try {
            int imageCountByUserId = imageDao.getImageCountByUserId(userId);
            return new Result(true, null, null, imageCountByUserId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getTitle() {
        ArrayList<Long> titles = imageDao.getTitle();
        return new Result(true, null, null, titles);
    }

    public Result getImageForGroup(String userId) {
        try {
            ArrayList<Long> metaTimes = imageDao.getImageMetaTime(userId);
            ArrayList<String> metaTimesStr = new ArrayList(metaTimes.size());
            ArrayList<ArrayList<ImageBean>> imagesForGroup = new ArrayList<>(metaTimes.size());
            for (long time : metaTimes) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                metaTimesStr.add(format.format(new Date(time)));
                ArrayList<ImageBean> images = imageDao.getImagesByMetaTime(userId, time);
                imagesForGroup.add(images);
            }
            ImageForGroupBean bean = new ImageForGroupBean();
            bean.setGroupList(metaTimesStr);
            bean.setItemList(imagesForGroup);
            return new Result(true, null, null, bean);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }

    }

    public Result getImageMetaTimes(String userId) {
        try {
            ArrayList<Long> imageMetaTime = imageDao.getImageMetaTime(userId);
            ArrayList<String> metaTimesStr = new ArrayList<>(imageMetaTime.size());
            for (long time : imageMetaTime) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                metaTimesStr.add(format.format(new Date(time)));
            }
            return new Result(true, null, null, metaTimesStr);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }


    public Result getAllImages(String userId, String fileCategory) {
        try {
            Calendar calendar = Calendar.getInstance();
            ArrayList<Long> yearMonth = imageDao.getYearMonth(userId, fileCategory);
            ArrayList<String> yearMonthList = new ArrayList<>(yearMonth.size());
            ArrayList<ArrayList<ImageBean>> imageList = new ArrayList<>(yearMonth.size());
            for (long time : yearMonth) {
                calendar.setTimeInMillis(time);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                if (10 > month + 1) {
                    yearMonthList.add(year + " 年 0" + (month + 1) + " 月");
                } else {
                    yearMonthList.add(year + " 年 " + (month + 1) + " 月");
                }
                ArrayList<ImageBean> images = imageDao.getImagesByYearMonth(time, userId, fileCategory);
                imageList.add(images);
            }
            ImageThumbnailBean bean = new ImageThumbnailBean();
            bean.setTitleList(yearMonthList);
            bean.setImageList(imageList);
            return new Result(true, null, null, bean);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result setCategoryPrivate(String categoryId, String password) {
        int count = imageDao.setCategoryPrivate(categoryId, password);
        if (count > 0) {
            return new Result(true, null, null, null);
        } else {
            return new Result(false, null, null, null);
        }
    }

    public Result setCategoryPublic(String categoryId) {
        int count = imageDao.setCategoryPublic(categoryId, StringUtil.string2MD5("", ""));
        if (count > 0) {
            return new Result(true, null, null, null);
        } else {
            return new Result(false, null, null, null);
        }
    }

    public Result getImageThumbnail(String userId, String categoryId) {
        ArrayList<ImageBean> images = imageDao.getImageThumbnail(userId, categoryId);
        return new Result(true, null, null, images);
    }

}
