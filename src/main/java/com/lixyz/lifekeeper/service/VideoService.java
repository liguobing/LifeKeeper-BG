package com.lixyz.lifekeeper.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.netdisk.video.*;
import com.lixyz.lifekeeper.bean.user.UserBean;
import com.lixyz.lifekeeper.dao.VideoDao;
import com.lixyz.lifekeeper.util.Constant;
import com.lixyz.lifekeeper.util.FileUtil;
import com.lixyz.lifekeeper.util.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.VideoSize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {
    private final VideoDao dao;

    public VideoService(VideoDao VideoDao) {
        this.dao = VideoDao;
    }

    public Result uploadVideo(MultipartFile mSourceFile, String videoBeanStr) {
        try {
            //文件为空
            if (mSourceFile.isEmpty()) {
                return new Result(false, "服务器出错，请稍候重试", new Exception("文件为空"), null);
            }
            VideoBean bean = new Gson().fromJson(videoBeanStr, VideoBean.class);

            File tmpVideo = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/tmp-video/" + bean.getSourceFileName());
            FileUtil.multipartFileTransferToFile(mSourceFile, tmpVideo);
            //是否已经存在
            if (dao.queryVideoSha1(FileUtil.getFileSha1(tmpVideo)) > 0) {
                tmpVideo.delete();
                return new Result(true, null, null, 1);
            }
            //检查目录是否存在，不存在就创建
            File dir = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser());
            if (!dir.exists()) {
                boolean mkdir = dir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            File thumbnailDir = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/thumbnail");
            if (!thumbnailDir.exists()) {
                boolean mkdir = thumbnailDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            File coverDir = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/cover");
            if (!coverDir.exists()) {
                boolean mkdir = coverDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            File blurDir = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/blur");
            if (!blurDir.exists()) {
                boolean mkdir = blurDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在，并且创建失败"), null);
                }
            }
            File videoDir = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/videos");
            if (!videoDir.exists()) {
                boolean mkdir = videoDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("目录不存在"), null);
                }
            }


            //查看是PC端还是移动端上传的（移动端VideoCategory为空，PC端有值）
            if (bean.getFileCategory() == null) {
                //如果是移动端上传的，就先将所有视频上传的“未分类”相册
                //先获取到“未分类”相册
                VideoCategoryBean category = dao.getDefaultCategory(bean.getVideoUser());
                //如果该用户没有“未分类”相册，则创建之
                if (category == null) {
                    VideoCategoryBean categoryBean = new VideoCategoryBean();
                    categoryBean.setObjectId(StringUtil.getRandomString());
                    categoryBean.setCategoryId(StringUtil.getRandomString());
                    categoryBean.setCategoryName("未分类");
                    categoryBean.setCategoryUser(bean.getVideoUser());
                    categoryBean.setIsPrivate(-1);
                    categoryBean.setPassword("");
                    categoryBean.setCategoryStatus(1);
                    categoryBean.setCategoryType(0);
                    categoryBean.setCreateTime(System.currentTimeMillis());
                    categoryBean.setUpdateTime(0);
                    long l = dao.addVideoCategory(categoryBean);
                    if (l > 0) {
                        bean.setFileCategory(categoryBean.getCategoryId());
                    } else {
                        return new Result(false, "添加默认分类出错", null, null);
                    }
                } else {
                    bean.setFileCategory(category.getCategoryId());
                }
            }
            return saveDataToSQL(mSourceFile, bean);
        } catch (SQLException | IOException | JsonSyntaxException | EncoderException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    private synchronized boolean setDefaultCategory(VideoBean bean) throws SQLException {
        //先查看有没有“未分类”这个类别
        VideoCategoryBean videoCategoryBean = dao.getDefaultCategoryByName(bean.getVideoUser());
        if (videoCategoryBean == null) {
            String categoryId = StringUtil.getRandomString();
            VideoCategoryBean categoryBean = new VideoCategoryBean();
            categoryBean.setObjectId(StringUtil.getRandomString());
            categoryBean.setCategoryId(categoryId);
            categoryBean.setCategoryName("未分类");
            categoryBean.setCategoryUser(bean.getVideoUser());
            categoryBean.setIsPrivate(-1);
            categoryBean.setPassword("");
            categoryBean.setCategoryStatus(1);
            categoryBean.setCategoryType(0);
            categoryBean.setCreateTime(System.currentTimeMillis());
            categoryBean.setUpdateTime(0);
            long l = dao.addVideoCategory(categoryBean);
            if (l > 0) {
                bean.setFileCategory(categoryId);
                return true;
            } else {
                return false;
            }
        } else {
            bean.setFileCategory(videoCategoryBean.getCategoryId());
            return true;
        }
    }

    private synchronized boolean createFileDir(VideoBean bean) {
        //先看看用户目录存在不存在，不存在的话，创建之
        File dir = new File(Constant.TOMCAT_FILE_PATH + "/webapps/LifeKeeperFiles/LifeKeeperPhoneVideo/" + bean.getVideoUser());
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
            if (!mkdir) {
                return false;
            }
        }
        //在用户目录下创建 Cover 目录
        File coverDir = new File(Constant.TOMCAT_FILE_PATH + "/webapps/LifeKeeperFiles/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/cover");
        if (!coverDir.exists()) {
            boolean mkdir = coverDir.mkdirs();
            if (!mkdir) {
                return false;
            }
        }
        //创建一个 TmpCover 目录，用来存储截屏文，截屏文件一般都挺大，需要转换成正式的 Cover 文件
        File tmpCoverDir = new File(Constant.TOMCAT_FILE_PATH + "/webapps/LifeKeeperFiles/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/cover/tmp");
        if (!tmpCoverDir.exists()) {
            boolean mkdir = tmpCoverDir.mkdirs();
            if (!mkdir) {
                return false;
            }
        }
        //在用户目录下创建 Thumbnail 目录
        File thumbnailDir = new File(Constant.TOMCAT_FILE_PATH + "/webapps/LifeKeeperFiles/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/thumbnail");
        if (!thumbnailDir.exists()) {
            boolean mkdir = thumbnailDir.mkdirs();
            if (!mkdir) {
                return false;
            }
        }
        //在用户目录下创建 Blur 目录
        File blurDir = new File(Constant.TOMCAT_FILE_PATH + "/webapps/LifeKeeperFiles/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/blur");
        if (!blurDir.exists()) {
            return blurDir.mkdirs();
        }
        return true;
    }

    private synchronized void saveFileToStorage(MultipartFile mSourceFile, VideoBean bean) throws IOException, EncoderException {
        //保存源文件
        File dest = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/videos/" + bean.getSourceFileName());
        boolean saveSourceFileResult = FileUtil.multipartFileTransferToFile(mSourceFile, dest);
        //源文件保存成功之后，开始处理缩略图等
        if (saveSourceFileResult) {
            //设置时长
            if (bean.getDuration() <= 0) {
                MultimediaObject instance = new MultimediaObject(dest);
                MultimediaInfo result = instance.getInfo();
                bean.setDuration(result.getDuration());
                VideoSize size = result.getVideo().getSize();
                bean.setVideoWidth(size.getWidth());
                bean.setVideoHeight(size.getHeight());
            }
            //保存 Cover 文件
            File coverFile = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/cover/" + bean.getCoverFileName());
            FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(dest.getAbsolutePath());
            ff.setFormat(FilenameUtils.getExtension(dest.getName()));
            ff.start();
            int ffLength = ff.getLengthInFrames();
            Frame f;
            int i = 0;
            while (i < ffLength) {
                f = ff.grabImage();
                //截取第4帧
                if ((i > 3) && (f.image != null)) {
                    //生成视频的相对路径 例如：pic/uuid.png
                    //执行截图并放入指定位置
                    Java2DFrameConverter converter = new Java2DFrameConverter();
                    BufferedImage bi = converter.getBufferedImage(f);
                    File output = new File(coverFile.getAbsolutePath());
                    boolean result = ImageIO.write(bi, "webp", output);
                    if (result) {
                        //保存 ThumbnailFile 文件
                        File thumbnail = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/thumbnail/" + bean.getThumbnailFileName());
                        boolean thumbnailResult = FileUtil.getThumbnailFile(coverFile, thumbnail);
                        if (thumbnailResult) {
                            //保存 Blur 文件
                            File blur = new File("/files/LifeKeeperPhoneVideo/" + bean.getVideoUser() + "/blur/" + bean.getBlurFileName());
                            FileUtil.getBlurFile(thumbnail, blur);
                        }
                    }
                    break;
                }
                i++;
            }
            ff.stop();
        }
    }

    private synchronized Result saveDataToSQL(MultipartFile mSourceFile, VideoBean bean) throws EncoderException, IOException, SQLException {
        boolean result = createFileDir(bean);
        if (result) {
            saveFileToStorage(mSourceFile, bean);
            long l = dao.insertVideo(
                    bean.getObjectId(),
                    bean.getVideoId(),
                    bean.getSha1(),
                    bean.getDuration(),
                    bean.getFileCategory(),
                    bean.getOriginalFileName(),
                    bean.getSourceFileName(),
                    bean.getCoverFileName(),
                    bean.getThumbnailFileName(),
                    bean.getBlurFileName(),
                    bean.getVideoUser(),
                    bean.getVideoStatus(),
                    bean.getCreateTime(),
                    bean.getVideoWidth(),
                    bean.getVideoHeight()
            );
            if (l > 0) {
                return new Result(true, null, null, dao.queryVideoCount(bean.getVideoUser()));
            } else {
                return new Result(false, "数据库插入数据出错", null, 0);
            }
        } else {
            return new Result(false, "创建目录出错", null, false);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result deleteVideoByObjectId(List<String> objectIdList) {
        try {
            int i = dao.deleteVideoByObjectId(objectIdList);
            if (i == objectIdList.size()) {
                return new Result(true, null, null, i > 0);
            } else {
                return new Result(false, "服务器出错，请稍候重试", null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public long queryVideoOverview(String userId) throws SQLException {
        return dao.queryVideoCount(userId);
    }


    public Result checkVideoCategoryPassword(String userId, String categoryId, String password) {
        try {
            if (dao.checkVideoCategoryPassword(categoryId, password, userId) > 0) {
                return new Result(true, null, null, true);
            } else {
                return new Result(false, null, null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result getVideoCategoryAndCover(String userId) {
        try {
            ArrayList<VideoCategoryBean> videoCategories = dao.getVideoCategories(userId);
            ArrayList<VideoCategoryCover> list = new ArrayList<>();
            for (VideoCategoryBean bean : videoCategories) {
                VideoBean videoBean = dao.getLatestVideoByCategory(bean.getCategoryId());
                if (videoBean != null) {
                    VideoCategoryCover cover = new VideoCategoryCover();
                    cover.setCategory(bean);
                    cover.setVideo(videoBean);
                    cover.setVideoCount(dao.getVideoCountByCategory(bean.getCategoryId(), userId));
                    list.add(cover);
                }
            }
            return new Result(true, null, null, list);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "出错啦", e, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result updateCategory(String bean) {
        try {
            VideoCategoryBean videoCategoryBean = new Gson().fromJson(bean, VideoCategoryBean.class);
            int count = dao.updateVideoCategory(videoCategoryBean);
            return new Result(count > 0, null, null, videoCategoryBean);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }


    public Result addVideoCategory(String category) {
        try {
            VideoCategoryBean bean = new Gson().fromJson(category, VideoCategoryBean.class);
            VideoCategoryBean videoCategoryBean = dao.videoCategoryNameIsExists(bean.getCategoryName(), bean.getCategoryUser());
            if (videoCategoryBean != null) {
                return new Result(true, null, null, videoCategoryBean.getCategoryId());
            } else {
                long l = dao.addVideoCategory(bean);
                if (l > 0) {
                    return new Result(true, null, null, bean.getCategoryId());
                } else {
                    return new Result(false, "添加视频分类失败", null, null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "添加视频分类失败", null, null);
        }
    }

    public Result getRangeVideoByCategoryWB(String categoryId, String userId, int offset, int rows) {
        try {
            if (rows == -1) {
                ArrayList<VideoBean> videos = dao.getRangeVideoByCategory(categoryId, userId, 0, Integer.MAX_VALUE);
                PageVideoBean bean = new PageVideoBean();
                bean.setVideos(videos);
                bean.setCategoryName("");
                bean.setVideoCount(0);
                bean.setCurrentPage(0);
                bean.setPageCount(0);
                bean.setPageSize(0);
                return new Result(true, null, null, bean);
            } else {
                ArrayList<VideoBean> videos = dao.getRangeVideoByCategory(categoryId, userId, (offset - 1) * rows, rows);
                String categoryName = dao.getVideoCategoryName(categoryId, userId);
                int videoCount = dao.getVideoCountByCategory(categoryId, userId);
                int pageCount;
                if (videoCount % rows == 0) {
                    pageCount = videoCount / rows;
                } else {
                    pageCount = (videoCount / rows) + 1;
                }
                PageVideoBean bean = new PageVideoBean();
                bean.setCategoryName(categoryName);
                bean.setVideoCount(videoCount);
                bean.setVideos(videos);
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

    public Result getOtherVideoCategory(String categoryId, String userId) {
        try {
            ArrayList<VideoCategoryBean> categoryList = dao.getOtherCategory(categoryId, userId);
            return new Result(true, null, null, categoryList);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result moveVideoWB(MoveVideoWBBean bean, String userId) {
        try {
            String targetCategoryId = bean.getTargetCategoryId();
            ArrayList<String> videoObjectIds = bean.getVideos();
            int count = dao.moveVideo(targetCategoryId, videoObjectIds, userId, System.currentTimeMillis());
            if (count == bean.getVideos().size()) {
                return new Result(true, null, null, true);
            } else {
                return new Result(false, "服务器出错，请稍候重试", null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result verifyVideoCategoryPassword(String userId, String categoryId, String password) {
        try {
            VideoCategoryBean videoCategory = dao.getVideoCategoryByCategoryId(categoryId);
            if (videoCategory.getIsPrivate() > 0) {
                return new Result(password.equals(videoCategory.getPassword()), null, null, null);
            } else {
                UserBean user = dao.getUserByUserId(userId);
                String inputPassword = StringUtil.string2MD5(user.getUserPhone(), password);
                return new Result(inputPassword.equals(user.getUserPassword()), null, null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result deleteVideoCategory(String userId, String categoryId) {
        try {
            int i = dao.deleteVideoCategory(userId, categoryId);
            return new Result(i > 0, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getVideoCount(String userId) {
        try {
            int videoCountByUserId = dao.getVideoCountByUserId(userId);
            return new Result(true, null, null, videoCountByUserId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result setCategoryPrivate(String categoryId, String password) {
        int count = dao.setCategoryPrivate(categoryId, password);
        if (count > 0) {
            return new Result(true, null, null, null);
        } else {
            return new Result(false, null, null, null);
        }
    }

    public Result setCategoryPublic(String categoryId) {
        int count = dao.setCategoryPublic(categoryId, StringUtil.string2MD5("", ""));
        if (count > 0) {
            return new Result(true, null, null, null);
        } else {
            return new Result(false, null, null, null);
        }
    }

    public Result getVideoThumbnail(String userId, String categoryId) {
        ArrayList<VideoBean> videos = dao.getVideoThumbnail(userId, categoryId);
        return new Result(true, null, null, videos);
    }
}
