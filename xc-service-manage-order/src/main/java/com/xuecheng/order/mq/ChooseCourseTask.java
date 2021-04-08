package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @Author zhn
 * @Date 2021/4/8 14:51
 * @Version 1.0
 */
@Component
public class ChooseCourseTask {
    private static final Logger LOGGER= LoggerFactory.getLogger(ChooseCourseTask.class);
    @Autowired
    TaskService taskService;

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChooseCourseTask(XcTask xcTask){
        if(xcTask!=null&& StringUtils.isNotEmpty(xcTask.getId())){
            taskService.finishTask(xcTask.getId());
        }
    }
    //定时发送添加选课任务
    @Scheduled(fixedRate = 3000)
    public void sendChooseCourseTask(){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskList(time, 100);
        System.out.println(xcTaskList);
        //发送消息队列
        for (XcTask xcTask : xcTaskList) {
            //取任务
            if(taskService.getTask(xcTask.getId(),xcTask.getVersion())>0){
                String ex = xcTask.getMqExchange();
                String routingkey = xcTask.getMqRoutingkey();
                taskService.publish(xcTask,ex,routingkey);
            }
        }
    }
    //定义任务调试策略
//    @Scheduled(cron = "0/3 * * * * *") //每隔3秒执行
//    @Scheduled(fixedRate = 3000) //在任务开始后3秒执行下一次调度
//    @Scheduled(fixedDelay = 3000)  //在任务结束后3秒才执行
    public void task1() throws InterruptedException {
        LOGGER.info("==============测试定时任务1开始===================");
        Thread.sleep(5000);
        LOGGER.info("==============测试定时任务1结束===================");
    }
    //定义任务调试策略
//    @Scheduled(cron = "0/3 * * * * *") //每隔3秒执行
//    @Scheduled(fixedRate = 3000) //在任务开始后3秒执行下一次调度
//    @Scheduled(fixedDelay = 3000)  //在任务结束后3秒才执行
    public void task2() throws InterruptedException {
        LOGGER.info("==============测试定时任务2开始===================");
        Thread.sleep(5000);
        LOGGER.info("==============测试定时任务2结束===================");
    }
}
