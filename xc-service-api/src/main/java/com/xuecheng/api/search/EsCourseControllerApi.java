package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * @Author zhn
 * @Date 2021/3/31 15:47
 * @Version 1.0
 */
@Api(value="课程搜索接口",description = "课程搜索接口")
public interface EsCourseControllerApi {
    //搜索课程信息
    @ApiOperation("课程搜索")
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam);
    @ApiOperation("根据课程id查询课程信息")
    public Map<String, CoursePub> getAll(String id);
    @ApiOperation("根据课程计划id查询课程媒资信息")
    public TeachplanMediaPub getMedia(String id);
}
