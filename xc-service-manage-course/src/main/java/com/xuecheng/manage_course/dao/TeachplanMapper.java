package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @Author zhn
 * @Date 2021/3/25 12:01
 * @Version 1.0
 */
@Mapper
@Component
public interface TeachplanMapper {
    public TeachplanNode selectList(String courseId);
}
