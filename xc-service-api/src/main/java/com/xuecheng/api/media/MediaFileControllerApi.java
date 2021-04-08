package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Author zhn
 * @Date 2021/4/4 19:58
 * @Version 1.0
 */
@Api(value="媒体文件接口",description = "媒体文件接口，提供页面的增、删、改、查")
public interface MediaFileControllerApi {
    @ApiOperation("我的媒资文件查询列表")
    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest);
}
