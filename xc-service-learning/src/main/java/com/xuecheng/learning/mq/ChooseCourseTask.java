package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.LearningService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author zhn
 * @Date 2021/4/8 16:53
 * @Version 1.0
 */
@Component
public class ChooseCourseTask {
    @Autowired
    LearningService learningService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE)
    public void receiveChooseCourseTask(XcTask xcTask){
        //添加选课
        String requestBody = xcTask.getRequestBody();
        Map map = JSON.parseObject(requestBody, Map.class);
        String userId= (String) map.get("userId");
        String courseId= (String) map.get("courseId");
        //添加选课
        ResponseResult responseResult = learningService.addCourse(userId, courseId, null, null, null, xcTask);
        if(responseResult.isSuccess()){
            //添加选课成功，向mq发送完成添加选课的消息
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY,xcTask);
        }
    }
}
