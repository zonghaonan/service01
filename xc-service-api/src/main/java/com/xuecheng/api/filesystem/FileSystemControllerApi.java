package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author zhn
 * @Date 2021/3/27 10:13
 * @Version 1.0
 */
@Api(value="文件管理接口",description = "文件管理接口，提供页面的增、删、改、查")
public interface FileSystemControllerApi {
    @ApiOperation("上传文件接口")
    public UploadFileResult upload(MultipartFile multipartFile,String filetag,String businesskey,String metadata);
}
