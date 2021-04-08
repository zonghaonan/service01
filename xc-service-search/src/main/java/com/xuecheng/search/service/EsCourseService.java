package com.xuecheng.search.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhn
 * @Date 2021/3/31 15:54
 * @Version 1.0
 */
@Service
public class EsCourseService {
    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.course.source_field}")
    private String source_field;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    //课程搜索
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam) {
        if (courseSearchParam==null){
            courseSearchParam=new CourseSearchParam();
        }
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
//        searchRequest.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //过滤源字段
        String[] source_field_array = source_field.split(",");
        sourceBuilder.fetchSource(source_field_array,new String[]{});
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //搜索条件
        //根据关键字搜索
        if (StringUtils.isNoneEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //根据一级分类
        if(StringUtils.isNoneEmpty(courseSearchParam.getMt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getMt()));

        }
        //根据二级分类
        if(StringUtils.isNoneEmpty(courseSearchParam.getSt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getSt()));

        }
        //根据难度等级
        if(StringUtils.isNoneEmpty(courseSearchParam.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
        sourceBuilder.query(boolQueryBuilder);
        //分页
        if(page<=0){
            page=1;
        }
        if(size<=0){
            size=10;
        }
        int from=(page-1)*size;
        sourceBuilder.from(from);
        sourceBuilder.size(size);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        sourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(sourceBuilder);
        QueryResult<CoursePub> queryResult = new QueryResult<>();
        List<CoursePub> list = new ArrayList<>();
        //执行搜索
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            long total = hits.getTotalHits();
            queryResult.setTotal(total);
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                CoursePub coursePub = new CoursePub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出id
                String id = (String)sourceAsMap.get("id");
                coursePub.setId(id);
                //取出name
                String name = (String) sourceAsMap.get("name");
                //取出高亮字段name
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(highlightFields!=null){
                    HighlightField highlightFieldName = highlightFields.get("name");
                    if(highlightFieldName!=null){
                        Text[] fragments = highlightFieldName.fragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for(Text text:fragments){
                            stringBuffer.append(text);
                        }
                        name = stringBuffer.toString();
                    }
                }
                coursePub.setName(name);
                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                Double price = null;
                try {
                    if(sourceAsMap.get("price")!=null){
                        price = (Double) sourceAsMap.get("price");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                //旧价格
                Double price_old = null;
                try {
                    if(sourceAsMap.get("price_old")!=null ){
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                list.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryResult.setList(list);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    public Map<String, CoursePub> getAll(String id) {
        //定义一个搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("id",id));
        searchRequest.source(sourceBuilder);
        CoursePub coursePub = new CoursePub();
        Map<String,CoursePub> map=new HashMap<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String  courseId  = (String)  sourceAsMap.get("id");
                String  name  = (String)  sourceAsMap.get("name");
                String  grade  = (String)  sourceAsMap.get("grade");
                String  charge  = (String)  sourceAsMap.get("charge");
                String  pic  = (String)  sourceAsMap.get("pic");
                String  description  = (String)  sourceAsMap.get("description");
                String  teachplan  = (String)  sourceAsMap.get("teachplan");
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                map.put(courseId,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    //根据多个课程计划id查询课程媒资信息
    public QueryResponseResult getMedia(String[] teachplanIds) {
        //定义一个搜索请求对象
        SearchRequest searchRequest = new SearchRequest(media_index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        String[] includes = media_source_field.split(",");
        sourceBuilder.fetchSource(includes,new String[]{});
        searchRequest.source(sourceBuilder);
        List<TeachplanMediaPub> teachplanMediaPubList=new ArrayList<>();
        long total=0;
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            total=hits.getTotalHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出课程计划媒资信息
                String  courseid  = (String)  sourceAsMap.get("courseid");
                String  media_id  = (String)  sourceAsMap.get("media_id");
                String  media_url  = (String)  sourceAsMap.get("media_url");
                String  teachplan_id  = (String)  sourceAsMap.get("teachplan_id");
                String  media_fileoriginalname  = (String)  sourceAsMap.get("media_fileoriginalname");
                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                //将数据加入列表
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryResult<TeachplanMediaPub> queryResult=new QueryResult<>();
        queryResult.setTotal(total);
        queryResult.setList(teachplanMediaPubList);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
}
