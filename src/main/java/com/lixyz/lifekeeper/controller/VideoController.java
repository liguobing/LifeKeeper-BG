package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.netdisk.video.MoveVideoWBBean;
import com.lixyz.lifekeeper.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@Api(tags = "视频管理相关接口")
public class VideoController {
    private final VideoService service;

    public VideoController(VideoService videoService) {
        this.service = videoService;
    }

    @PostMapping("/UploadVideo")
    @ApiOperation("上传视频的接口")
    public Result uploadVideo2(HttpServletRequest request,
                               @RequestParam("sourceFile") MultipartFile sourceFile,
                               @RequestParam("videoBean") String videoBean) {
        String token = request.getHeader("Token");
        if (token == null) {
            return new Result(false, "未登录操作", null, null);
        }
        return service.uploadVideo(sourceFile, videoBean);
    }

    @GetMapping("/GetVideoCategoryAndCover")
    @ApiOperation("获取用户视频分类以及封面")
    public Result getVideoCategoryAndCover(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getVideoCategoryAndCover(userId);
        }
    }

    @PostMapping(value = "EditVideoCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("更新视频分类")
    public Result updateCategory(HttpServletRequest request,
                                 @RequestBody String bean) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.updateCategory(bean);
        }
    }

    @GetMapping(value = "DeleteVideoCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("删除视频分类")
    public Result deleteVideoCategory(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String categoryId = request.getHeader("CategoryId");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.deleteVideoCategory(userId, categoryId);
        }
    }

    @PostMapping(value = "AddVideoCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("添加视频分类")
    public Result addVideoCategory(HttpServletRequest request,
                                   @RequestBody String category) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.addVideoCategory(category);
        }
    }

    @GetMapping(value = "CheckVideoCategoryPassword", produces = "application/json;charset=UTF-8")
    @ApiOperation("添加视频分类")
    public Result checkVideoCategoryPassword(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String password = request.getHeader("Password");
        String categoryId = request.getHeader("CategoryId");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.checkVideoCategoryPassword(userId, categoryId, password);
        }
    }

    @GetMapping(value = "GetVideos", produces = "application/json;charset=UTF-8")
    @ApiOperation("获取分类下的视频，分页形式")
    public Result getVideosByCategoryId(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            String categoryId = request.getHeader("CategoryId");
            String password = request.getHeader("Password");
            //先判断密码是否正确
            Result result = service.checkVideoCategoryPassword(userId, categoryId, password);
            if (result.getResult()) {
                //currentPage
                int currentPage = Integer.parseInt(request.getParameter("cp"));
                //pageSize
                int pageSize = Integer.parseInt(request.getParameter("ps"));
                return service.getRangeVideoByCategoryWB(categoryId, userId, currentPage, pageSize);
            } else {
                return new Result(false, null, null, null);
            }
        }
    }

    @GetMapping(value = "/GetVideoThumbnail")
    @ApiOperation("获取网盘视频预览数据（针对微信小程序）")
    public Result getVideoThumbnail(HttpServletRequest request) {
        String userId = request.getHeader("UserId");
        String categoryId = request.getHeader("CategoryId");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getVideoThumbnail(userId,categoryId);
        }
    }

    @GetMapping(value = "GetOtherVideoCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("获取其他视频分类")
    public Result getOtherVideoCategory(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        //categoryId
        String categoryId = request.getHeader("Category");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getOtherVideoCategory(categoryId, userId);
        }
    }

    @PostMapping("/MoveVideos")
    @ApiOperation("移动视频")
    public Result moveVideoWB(@RequestBody MoveVideoWBBean bean, HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.moveVideoWB(bean, userId);
        }
    }

    @PostMapping("/DeleteVideo")
    @ApiOperation("删除视频")
    public Result deleteVideo(HttpServletRequest request, @RequestBody List<String> videoObjectIdList) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.deleteVideoByObjectId(videoObjectIdList);
        }
    }

    @GetMapping(value = "SetVideoCategoryPrivate", produces = "application/json;charset=UTF-8")
    @ApiOperation("修改分类私密状态")
    public Result setVideoCategoryPrivate(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String categoryId = request.getHeader("CategoryId");
        String password = request.getHeader("Password");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.setCategoryPrivate(categoryId, password);
        }
    }

    @GetMapping(value = "SetVideoCategoryPublic", produces = "application/json;charset=UTF-8")
    @ApiOperation("修改分类开放状态")
    public Result setVideoCategoryPublic(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String categoryId = request.getHeader("CategoryId");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.setCategoryPublic(categoryId);
        }
    }

    @GetMapping(value = "VerifyVideoCategoryPassword", produces = "application/json;charset=UTF-8")
    @ApiOperation("检查视频分类密码")
    public Result verifyCategoryPassword(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String password = request.getHeader("Password");
        String categoryId = request.getHeader("CategoryId");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.verifyVideoCategoryPassword(userId, categoryId, password);
        }
    }

    @GetMapping(value = "GetVideoCount", produces = "application/json;charset=UTF-8")
    public Result getImageCount(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getVideoCount(userId);
        }
    }
}
