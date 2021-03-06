package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author zhn
 * @Date 2021/3/25 12:30
 * @Version 1.0
 */
@Service
public class CourseService {

    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CourseMarketRepository courseMarketRepository;
    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CoursePubRepository coursePubRepository;
    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;
    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;
    @Value("${course???publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course???publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course???publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course???publish.siteId}")
    private String publish_siteId;
    @Value("${course???publish.templateId}")
    private String publish_templateId;
    @Value("${course???publish.previewUrl}")
    private String previewUrl;
    //??????????????????
    public TeachplanNode findTeachplanList(String courseId){
        return teachplanMapper.selectList(courseId);
    }
    //??????????????????
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan){
        if(teachplan==null|| StringUtils.isEmpty(teachplan.getCourseid())||StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        }
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        //??????????????????????????????
        if(parentid==null){
            parentid = getTeachplanRoot(courseid);
        }
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        Teachplan parentNode = optional.get();
        String grade = parentNode.getGrade();
        Teachplan teachplanNew=new Teachplan();
        BeanUtils.copyProperties(teachplan,teachplanNew);
        teachplanNew.setParentid(parentid);
        if(grade.equals("1")){
            teachplanNew.setGrade("2");
        }else {
            teachplanNew.setGrade("3");
        }
        teachplanRepository.save(teachplanNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //???????????????????????????????????????????????????????????????
    private String getTeachplanRoot(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            return null;
        }
        //???????????????
        CourseBase courseBase = optional.get();
        //????????????????????????
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if(teachplanList==null||teachplanList.size()<=0){
            //????????????????????????????????????
            Teachplan teachplan=new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            System.out.println("1:"+teachplan.getId());
            teachplanRepository.save(teachplan);
            System.out.println("2"+teachplan.getId());
            return teachplan.getId();
        }
        //???????????????id
        return teachplanList.get(0).getId();
    }
    //????????????????????????
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest){
        if(page<=0){
            page=1;
        }
        if(size<=0){
            size=10;
        }
        if(courseListRequest==null){
            courseListRequest=new CourseListRequest();
        }
        PageHelper.startPage(page,size);
        Page<CourseInfo> courseList = courseMapper.findCourseList(courseListRequest.getCompanyId());
        List<CourseInfo> result = courseList.getResult();
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(result);
        queryResult.setTotal(courseList.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
    //??????????????????
    public ResponseResult addCoursePic(String courseId,String pic){
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        CoursePic coursePic=null;
        if(optional.isPresent()){
            coursePic=optional.get();
        }else {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //??????????????????
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    //??????????????????
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long result = coursePicRepository.deleteByCourseid(courseId);
        if(result>0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //????????????????????????????????????????????????????????????????????????
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();
        //????????????????????????
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if(courseBaseOptional.isPresent()){
            CourseBase courseBase = courseBaseOptional.get();
            courseView.setCourseBase(courseBase);
        }
        //??????????????????
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        if(coursePicOptional.isPresent()){
            CoursePic coursePic = coursePicOptional.get();
            courseView.setCoursePic(coursePic);
        }
        //????????????????????????
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if(courseMarketOptional.isPresent()){
            CourseMarket courseMarket = courseMarketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        //????????????????????????
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    //??????id????????????????????????
    public CourseBase findCourseBaseById(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }
    //????????????
    public CoursePublishResult preview(String id) {
        CourseBase courseBaseById = findCourseBaseById(id);
        //??????cms????????????
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBaseById.getName());
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setTemplateId(publish_templateId);
        //????????????cms
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if(!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        String url=previewUrl+pageId;
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    //????????????
    @Transactional
    public CoursePublishResult publish(String id) {
        CourseBase courseBaseById = findCourseBaseById(id);
        //??????cms????????????
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBaseById.getName());
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setTemplateId(publish_templateId);
        CmsPostPageResult result = cmsPageClient.postPageQuick(cmsPage);
        if(!result.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //??????????????????
        CourseBase courseBase = saveCoursePubState(id);
        if(courseBase==null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //????????????????????????
        //????????????coursePub??????
        CoursePub coursePub = createCoursePub(id);
        //???coursePub??????????????????
        saveCoursePub(id, coursePub);
        //??????????????????
        //...
        //????????????url
        String pageUrl = result.getPageUrl();
        //???teachplanMediaPub???????????????????????????
        saveTeachplanMediaPub(id);
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }
    //???teachplanMediaPub???????????????????????????
    private void saveTeachplanMediaPub(String courseId){
        //?????????teachplanMediaPub????????????
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        //???teachplanMedia?????????
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
        List<TeachplanMediaPub> teachplanMediaPubs=new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            //???????????????
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
    }
    //????????????????????????????????????202002
    private CourseBase saveCoursePubState(String courseId){
        CourseBase courseBaseById = findCourseBaseById(courseId);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }
    //???coursePub??????????????????
    private CoursePub saveCoursePub(String id,CoursePub coursePub){
        CoursePub coursePubNew=null;
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if(coursePubOptional.isPresent()){
            coursePubNew=coursePubOptional.get();
        }else {
            coursePubNew=new CoursePub();
        }
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //?????????
        coursePubNew.setTimestamp(new Date());
        //????????????
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }
    //??????coursePub??????
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //????????????id??????course_base
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            BeanUtils.copyProperties(courseBase,coursePub);
        }
        //????????????id??????????????????
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic,coursePub);
        }
        //??????????????????
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket,coursePub);
        }
        //??????????????????
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(jsonString);
        return coursePub;
    }

    //??????????????????????????????????????????
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {
        if(teachplanMedia==null||StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        }
        //?????????????????????????????????
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_ISNULL);
        }
        Teachplan teachplan = optional.get();
        String grade = teachplan.getGrade();
        if(StringUtils.isEmpty(grade)||!grade.equals("3")){
            //??????????????????????????????????????????
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        Optional<TeachplanMedia> mediaOptional = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one=null;
        if(mediaOptional.isPresent()){
            one=mediaOptional.get();
        }else {
            one=new TeachplanMedia();
        }
        //???one??????????????????
        one.setCourseId(teachplanMedia.getCourseId());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanId);
        teachplanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
