package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author zhn
 * @Date 2021/4/8 16:37
 * @Version 1.0
 */
public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse,String> {
    //根据用户id和课程id查询
    XcLearningCourse findByUserIdAndCourseId(String userId,String CourseId);
}
