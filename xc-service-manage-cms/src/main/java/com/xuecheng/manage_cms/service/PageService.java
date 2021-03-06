package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import net.bytebuddy.asm.Advice;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    TemplateService templateService;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    CmsSiteRepository cmsSiteRepository;
    //????????????
    //page???1????????????
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
        if(page<=0){
            page=1;
        }
        if(size<=0){
            size=0;
        }
        CmsPage cmsPage = new CmsPage();
        if(queryPageRequest!=null){
            if(StringUtils.isNoneEmpty(queryPageRequest.getPageAliase())){
                cmsPage.setPageAliase(queryPageRequest.getPageAliase());
            }
            if(StringUtils.isNoneEmpty(queryPageRequest.getSiteId())){
                cmsPage.setSiteId(queryPageRequest.getSiteId());
            }
            if(StringUtils.isNoneEmpty(queryPageRequest.getTemplateId())){
                cmsPage.setTemplateId(queryPageRequest.getTemplateId());
            }
        }
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //???????????????
        exampleMatcher=exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //??????Example
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //????????????1??????
        Pageable pageable= PageRequest.of(page-1,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult queryResponseResult=new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
//    //????????????
//    public CmsPageResult add(CmsPage cmsPage){
//        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
//        //???????????????????????????
//        if(cmsPage1==null){
//            cmsPage.setPageId(null);
//            cmsPageRepository.save(cmsPage);
//            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
//        }
//        //????????????
//        return new CmsPageResult(CommonCode.FAIL,null);
//    }
    //????????????
    public CmsPageResult add(CmsPage cmsPage){
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(cmsPage1!=null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //???????????????????????????
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }
    //????????????id??????????????????
    public CmsPageResult getPageById(String id){
        CmsPage cmsPage = this.getById(id);
        if(cmsPage!=null){
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    public CmsPage getById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
    //????????????
    public CmsPageResult update(String id,CmsPage cmsPage){
        CmsPage one = this.getById(id);
        if(one!=null){
            //????????????id
            one.setTemplateId(cmsPage.getTemplateId());
            //??????????????????
            one.setSiteId(cmsPage.getSiteId());
            //??????????????????
            one.setPageAliase(cmsPage.getPageAliase());
            //??????????????????
            one.setPageName(cmsPage.getPageName());
            //??????????????????
            one.setPageWebPath(cmsPage.getPageWebPath());
            //??????????????????
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //??????dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //????????????
            CmsPage save = cmsPageRepository.save(one);
            if(save!=null){
                return new CmsPageResult(CommonCode.SUCCESS,save);
            }
        }
        //????????????
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //??????id????????????
    public ResponseResult delete(String id){
        CmsPage cmsPage = this.getById(id);
        if(cmsPage!=null){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
    //?????????????????????
    public String getPageHtml(String pageId){
        //??????????????????
        Map model = getModelByPageId(pageId);
        if(model==null){
            //????????????????????????
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //???????????????????????????
        String template = getTemplateByPageId(pageId);
        if(template==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //???????????????
        return generateHtml(template, model);
    }
    //???????????????
    public String generateHtml(String templateContent,Map model){
        //???????????????
        Configuration configuration = new Configuration(Configuration.getVersion());
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        //?????????????????????????????????
        configuration.setTemplateLoader(stringTemplateLoader);
        //??????????????????
        Template template = null;
        try {
            template = configuration.getTemplate("template", "utf-8");
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //????????????????????????
    private String getTemplateByPageId(String pageId) {
        CmsPage cmsPage = getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String templateId = cmsPage.getTemplateId();
        if(StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        CmsTemplate cmsTemplate = templateService.findById(templateId);
        String templateFileId = cmsTemplate.getTemplateFileId();
        //????????????id????????????
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        //???????????????????????????
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //??????GridFsResource??????????????????
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //??????????????????
        String content = null;
        try {
            content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    //??????????????????
    private Map getModelByPageId(String pageId){
        CmsPage cmsPage = getById(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity;
        forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }
    //????????????
    public ResponseResult post(String pageId){
        //?????????????????????
        String pageHtml = getPageHtml(pageId);
        //?????????????????????????????????GridFs???
        saveHtml(pageId,pageHtml);
        //???MQ?????????
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //???mq????????????
    private void sendPostPage(String pageId){
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        }
        Map<String,String> msg=new HashMap<>();
        msg.put("pageId",pageId);
        String jsonString = JSON.toJSONString(msg);
        //??????id
        String siteId = cmsPage.getSiteId();
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
    }
    //??????html???GridFs
    private CmsPage saveHtml(String pageId,String htmlContent){
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage==null)
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        ObjectId objectId=null;
        //???htmlContent?????????????????????
        try {
            InputStream inputStream = IOUtils.toInputStream(htmlContent,"utf-8");
            //???html?????????????????????GridFs
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //???html??????id?????????cmsPage???
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    //?????????????????????????????????????????????
    public CmsPageResult save(CmsPage cmsPage) {
        CmsPage one = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(one!=null){
            return update(one.getPageId(),cmsPage);
        }
        return add(cmsPage);
    }

    //??????????????????
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //????????????????????????cms_page???
        CmsPageResult save = save(cmsPage);
        if (!save.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage saveCmsPage = save.getCmsPage();
        String pageId = saveCmsPage.getPageId();
        ResponseResult post = post(pageId);
        if(!post.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //????????????Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        String siteId = saveCmsPage.getSiteId();
        CmsSite cmsSite = findCmsSiteById(siteId);
        String pageUrl=cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+saveCmsPage.getPageWebPath()+cmsPage.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }
    //????????????id??????????????????
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite>  optional  = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return  optional.get();
        }
        return  null;
    }
}
