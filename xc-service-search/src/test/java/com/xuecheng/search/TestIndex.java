package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhn
 * @Date 2021/3/29 15:12
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {
//    @Autowired
//    @Qualifier("restHighLevelClient")
//    RestHighLevelClient client;
//    @Autowired
//    @Qualifier("restClient")
//    RestClient restClient;
//    //创建索引
//    @Test
//    public void testCreateIndex() throws IOException {
//        //创建索引的对象
//        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
//        //设置参数
//        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
////        //指定映射
////        createIndexRequest.mapping("{ \"properties\": {\n" +
////                "\t\"name\": {\n" +
////                "\t\t\"type\": \"text\", \n" +
////                "\t\t\"analyzer\":\"ik_max_word\", \n" +
////                "\t\t\"search_analyzer\":\"ik_smart\" \n" +
////                "\t}, \n" +
////                "\t\"description\": { \n" +
////                "\t\t\"type\": \"text\", \n" +
////                "\t\t\"analyzer\":\"ik_max_word\", \n" +
////                "\t\t\"search_analyzer\":\"ik_smart\" \n" +
////                "\t},\n" +
////                "\t\"pic\":{ \n" +
////                "\t\t\"type\":\"text\", \n" +
////                "\t\t\"index\":false \n" +
////                "\t},\n" +
////                "\t\"studymodel\":{ \n" +
////                "\t\t\"type\":\"text\" \n" +
////                "\t} \n" +
////                "} \n" +
////                "}", XContentType.JSON);
//        //操作索引的客户端
//        IndicesClient indices = client.indices();
//        //执行创建索引
//        AcknowledgedResponse delete = indices.create(createIndexRequest, RequestOptions.DEFAULT);
//        System.out.println(delete.isAcknowledged());
//    }
//    //删除索引
//    @Test
//    public void testDeleteIndex() throws IOException {
//        //删除索引的对象
//        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
//        //操作索引的客户端
//        IndicesClient indices = client.indices();
//        //执行删除索引
//        AcknowledgedResponse delete = indices.delete(deleteIndexRequest, RequestOptions.DEFAULT);
//        System.out.println(delete.isAcknowledged());
//    }
//    //添加文档
//    @Test
//    public void testAddDoc() throws IOException {
//        //准备json数据
//        Map<String, Object> jsonMap = new HashMap<>();
//        jsonMap.put("name", "spring cloud实战");
//        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
//        jsonMap.put("studymodel", "201001");
//        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
//        jsonMap.put("timestamp", dateFormat.format(new Date()));
//        jsonMap.put("price", 5.6f);
//        IndexRequest indexRequest = new IndexRequest("xc_course");
//        indexRequest.source(jsonMap);
//        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
//        DocWriteResponse.Result result = indexResponse.getResult();
//        System.out.println(result);
//    }
//    //查询文档
//    @Test
//    public void testGetDoc() throws IOException {
//        GetRequest getRequest = new GetRequest("xc_course", "D1jofHgBM6cTsMTTBD6l");
//        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
//        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
//        System.out.println(sourceAsMap);
//    }
}
