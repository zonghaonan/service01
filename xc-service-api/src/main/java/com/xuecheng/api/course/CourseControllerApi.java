package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.ui.Model;

/**
 * @Author zhn
 * @Date 2021/3/25 10:34
 * @Version 1.0
 */
@Api(value="cms课程管理接口",description = "cms课程管理接口，提供页面的增、删、改、查")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);
    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);
    @ApiOperation("分页查询课程列表")
    public ResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);
    @ApiOperation("添加课程图片")
    public ResponseResult addCoursePic(String courseId, String pic);
    @ApiOperation("查询课程图片")
    public CoursePic findCoursePic(String courseId);
    @ApiOperation("删除课程图片")
    public ResponseResult deleteCoursePic(String courseId);
    @ApiOperation("课程试图查询")
    public CourseView courseView(String id);
    @ApiOperation("课程预览")
    public CoursePublishResult preview(String id);
    @ApiOperation("课程发布")
    public CoursePublishResult publish(String id);
    @ApiOperation("保存课程计划与媒资文件的关联")
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia);
}
