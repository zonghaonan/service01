package com.xuecheng.order.service;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author zhn
 * @Date 2021/4/8 15:18
 * @Version 1.0
 */
@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;
    //查询前n条任务
    public List<XcTask> findXcTaskList(Date updateTime,int size){
        //设置分页参数
        Pageable pageable= PageRequest.of(0,size);
        Page<XcTask> all = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        List<XcTask> result = all.getResult();
        return result;
    }
    //发布消息
    public void publish(XcTask xcTask,String ex,String routingKey){
        Optional<XcTask> optional = xcTaskRepository.findById(xcTask.getId());
        if(optional.isPresent()){
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            //更新任务时间
            XcTask one = optional.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }
    //获取任务
    @Transactional
    public int getTask(String id,int version){
        //通过乐观锁的方式来更新数据库
        return xcTaskRepository.updateVersion(id, version);
    }
    //完成任务
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> optional = xcTaskRepository.findById(taskId);
        if(optional.isPresent()){
            XcTask xcTask = optional.get();
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }
}
