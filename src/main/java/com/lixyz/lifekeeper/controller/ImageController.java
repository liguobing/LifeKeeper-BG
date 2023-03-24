package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.netdisk.image.MoveImageWBBean;
import com.lixyz.lifekeeper.service.ImageService;
import com.lixyz.lifekeeper.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;

@RestController
@CrossOrigin
@Api(tags = "图片相关接口")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "AddImageCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("添加相册分类")
    public Result addNewImageCategory(HttpServletRequest request,
                                      @RequestBody String category) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.addImageCategory(category);
        }
    }


    @PostMapping(value = "EditImageCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("更新相册分类")
    public Result updateImageCategory(HttpServletRequest request,
                                      @RequestBody String bean) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.updateCategory(bean);
        }
    }

    @GetMapping(value = "SetImageCategoryPrivate", produces = "application/json;charset=UTF-8")
    @ApiOperation("修改相册私密状态")
    public Result setImageCategoryPrivate(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String categoryId = request.getHeader("CategoryId");
        String password = request.getHeader("Password");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.setCategoryPrivate(categoryId, password);
        }
    }

    @GetMapping(value = "SetImageCategoryPublic", produces = "application/json;charset=UTF-8")
    @ApiOperation("修改相册开放状态")
    public Result setImageCategoryPublic(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String categoryId = request.getHeader("CategoryId");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.setCategoryPublic(categoryId);
        }
    }


    @GetMapping(value = "DeleteImageCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("删除照片分类")
    public Result deleteImageCategory(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String categoryId = request.getHeader("CategoryId");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.deleteImageCategory(userId, categoryId);
        }
    }


    @GetMapping(value = "VerifyImageCategoryPassword", produces = "application/json;charset=UTF-8")
    @ApiOperation("检查相册分类密码")
    public Result verifyCategoryPassword(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        String password = request.getHeader("Password");
        String categoryId = request.getHeader("CategoryId");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.verifyImageCategoryPassword(userId, categoryId, password);
        }
    }

    @GetMapping(value = "GetImageCount", produces = "application/json;charset=UTF-8")
    @ApiOperation("检查相册分类密码")
    public Result getImageCount(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.getImageCount(userId);
        }
    }


    @GetMapping(value = "GetOtherImageCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("获取其他相册")
    public Result getOtherImageCategory(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            String categoryId = request.getHeader("CategoryId");
            return imageService.getOtherImageCategory(userId, categoryId);
        }
    }

    @PostMapping("/MoveImages")
    @ApiOperation("移动照片")
    public Result moveImage(@RequestBody MoveImageWBBean bean, HttpServletRequest request) {

        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.moveImages(bean, userId);
        }
    }

    @GetMapping(value = "GetImages", produces = "application/json;charset=UTF-8")
    @ApiOperation("获取分类下的照片，分页形式")
    public Result getImagesByCategoryId(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            String categoryId = request.getHeader("CategoryId");
            String password = request.getHeader("Password");
            //先判断密码是否正确
            Result result = imageService.checkImageCategoryPassword(userId, categoryId, password);
            if (result.getResult()) {
                //currentPage
                int currentPage = Integer.parseInt(request.getParameter("cp"));
                //pageSize
                int pageSize = Integer.parseInt(request.getParameter("ps"));
                return imageService.getRangeImagesByCategory(categoryId, userId, currentPage, pageSize);
            } else {
                return new Result(false, "密码出错", null, null);
            }
        }
    }

    @GetMapping(value = "/GetImageThumbnail")
    @ApiOperation("获取网盘图片预览数据（针对微信小程序）")
    public Result getImageThumbnail(HttpServletRequest request) {
        String userId = request.getHeader("UserId");
        String categoryId = request.getHeader("CategoryId");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.getImageThumbnail(userId, categoryId);
        }
    }


    @PostMapping("/UploadImage")
    @ApiOperation("上传图片")
    public Result uploadImage(HttpServletRequest request,
                              @RequestParam("sourceFile") MultipartFile sourceFile,
                              @RequestParam("imageBean") String imageBean) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.uploadImage(sourceFile, imageBean);
        }
    }

    @GetMapping("/GetImageCategoryAndCover")
    @ApiOperation("获取用户照片分类以及封面")
    public Result getImageCategoryAndCover(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.getImageCategoryAndCover(userId);
        }
    }

    @PostMapping("/DeleteImage")
    @ApiOperation("删除图片")
    public Result deleteImage(HttpServletRequest request, @RequestBody ArrayList<String> objectIds) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.deleteImages(objectIds);
        }
    }


    @GetMapping("/GetImageMateTimes")
    public Result getImageMateTimes(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return imageService.getImageMetaTimes(userId);
        }
    }

    @GetMapping(value = "/GetAllImages")
    @ApiOperation("获取照片")
    public Result getAllImages(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            String categoryId = request.getHeader("CategoryId");
            String password = request.getHeader("Password");
            //先判断密码是否正确
            Result result = imageService.checkImageCategoryPassword(userId, categoryId, StringUtil.string2MD5("", password));
            if (result.getResult()) {
                return imageService.getAllImages(userId, categoryId);
            } else {
                return new Result(false, null, null, null);
            }
        }
    }
}
