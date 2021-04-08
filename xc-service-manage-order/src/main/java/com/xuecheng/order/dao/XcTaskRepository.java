package com.xuecheng.order.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * @Author zhn
 * @Date 2021/4/8 15:16
 * @Version 1.0
 */
public interface XcTaskRepository extends JpaRepository<XcTask,String> {
    //查询某个时间之前的前n条任务
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);
    //更新updateTime
    @Modifying
    @Query("update XcTask t set t.updateTime=:updateTime where t.id=:id")
    public int updateTaskTime(@Param(value = "id") String id,@Param("updateTime") Date updateTime);
    @Modifying
    @Query("update XcTask t set t.version=:version+1 where t.id=:id and t.version=:version")
    public int updateVersion(@Param(value = "id") String id,@Param("version") int version);
}
