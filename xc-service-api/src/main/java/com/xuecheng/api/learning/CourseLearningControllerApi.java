package com.xuecheng.api.learning;

import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Author zhn
 * @Date 2021/4/5 14:51
 * @Version 1.0
 */
@Api(value="录播课程学习管理",description = "录播课程学习管理")
public interface CourseLearningControllerApi {
    @ApiOperation("获取课程学习地址")
    public GetMediaResult getMedia(String courseId,String teachplanId);
}
