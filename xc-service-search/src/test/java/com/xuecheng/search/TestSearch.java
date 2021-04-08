package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.text.ParseException;
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
public class TestSearch {
//    @Autowired
//    @Qualifier("restHighLevelClient")
//    RestHighLevelClient client;
//    @Autowired
//    @Qualifier("restClient")
//    RestClient restClient;
//    //搜索全部记录
//    @Test
//    public void testSearchAll() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        //matchAllQuery搜索全部
//        sourceBuilder.query(QueryBuilders.matchAllQuery());
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//        }
//    }
//
//    //分页查询
//    @Test
//    public void testSearchPage() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        //matchAllQuery搜索全部
//        sourceBuilder.query(QueryBuilders.matchAllQuery());
//        //设置分页参数
//        int page=1;
//        int size=1;
//        int from=(page-1)*size;
//        //计算下标
//        sourceBuilder.from(from);
//        sourceBuilder.size(size);
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//        }
//    }
//    //TermQuery
//    @Test
//    public void testTermQuery() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        //设置分页参数
//        int page=1;
//        int size=1;
//        int from=(page-1)*size;
//        //计算下标
//        sourceBuilder.from(from);
//        sourceBuilder.size(size);
//        //matchAllQuery搜索全部
//        sourceBuilder.query(QueryBuilders.termQuery("name","spring"));
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//        }
//    }
//    //根据id查询
//    @Test
//    public void testTermQueryById() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        String[] ids=new String[]{"1","2"};
//        //根据id查询
//        sourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//        }
//    }
//    //MatchQuery
//    @Test
//    public void testMatchQuery() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        //根据id查询
//        sourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("80%"));
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//        }
//    }
//    //MultiMatchQuery
//    @Test
//    public void testMultiMatchQuery() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        //根据id查询
//        sourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description")
//                .minimumShouldMatch("50%")
//                .field("name",10));
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//        }
//    }
//    //BoolQuery
//    @Test
//    public void testBoolQuery() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
//                .minimumShouldMatch("50%")
//                .field("name", 10);
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(multiMatchQueryBuilder).must(termQueryBuilder);
//        sourceBuilder.query(boolQueryBuilder);
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//        }
//    }
//    //filter
//    @Test
//    public void testFilter() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
//                .minimumShouldMatch("50%")
//                .field("name", 10);
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(multiMatchQueryBuilder);
//        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
//        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(90).lte(100));
//        sourceBuilder.query(boolQueryBuilder);
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//            System.out.println(studymodel);
//            System.out.println(price);
//        }
//    }
//    //排序
//    @Test
//    public void testSort() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
//        sourceBuilder.query(boolQueryBuilder);
//        sourceBuilder.sort("studymodel", SortOrder.DESC).sort("price",SortOrder.ASC);
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//            System.out.println(studymodel);
//            System.out.println(price);
//        }
//    }
//    //高亮
//    @Test
//    public void testHighlight() throws IOException, ParseException {
//        //搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("xc_course");
//        //搜索源构建对象
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发框架", "name", "description")
//                .minimumShouldMatch("50%")
//                .field("name", 10);
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(multiMatchQueryBuilder);
//        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
//        sourceBuilder.query(boolQueryBuilder);
//        //设置字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        sourceBuilder.fetchSource(new String[]{"name", "studymodel", "price","timestamp","description"}, new String[]{});
//        //设置高亮
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.preTags("<tag>");
//        highlightBuilder.postTags("<tag>");
//        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
//        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
//        sourceBuilder.highlighter(highlightBuilder);
//        //向搜索请求对象中设置搜索源
//        searchRequest.source(sourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;
//        System.out.println(totalHits);
//        SearchHit[] searchHits = hits.getHits();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (SearchHit hit : searchHits) {
//            //文档主键
//            String id = hit.getId();
//            //源文档内容
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String name = (String) sourceAsMap.get("name");
//            //取出高亮字段
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//            if (highlightFields!=null){
//                HighlightField description = highlightFields.get("description");
//                if(description!=null){
//                    Text[] fragments = description.getFragments();
//                    StringBuffer stringBuffer=new StringBuffer();
//                    for (Text text : fragments) {
//                        stringBuffer.append(text);
//                    }
//                    System.out.println(stringBuffer);
//                }
//            }
//            String description = (String) sourceAsMap.get("description");
//            String studymodel = (String) sourceAsMap.get("studymodel");
//            Double price = (Double) sourceAsMap.get("price");
//            Date date = dateFormat.parse((String) sourceAsMap.get("timestamp"));
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println(date);
//            System.out.println(studymodel);
//            System.out.println(price);
//        }
//    }
}
