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
    //页面查询
    //page从1开始计数
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
        //条件匹配器
        exampleMatcher=exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //定义Example
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //让页面从1开始
        Pageable pageable= PageRequest.of(page-1,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult queryResponseResult=new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
//    //新增页面
//    public CmsPageResult add(CmsPage cmsPage){
//        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
//        //如果查询不到就添加
//        if(cmsPage1==null){
//            cmsPage.setPageId(null);
//            cmsPageRepository.save(cmsPage);
//            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
//        }
//        //添加失败
//        return new CmsPageResult(CommonCode.FAIL,null);
//    }
    //新增页面
    public CmsPageResult add(CmsPage cmsPage){
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(cmsPage1!=null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //如果查询不到就添加
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }
    //根据页面id查询页面信息
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
    //修改页面
    public CmsPageResult update(String id,CmsPage cmsPage){
        CmsPage one = this.getById(id);
        if(one!=null){
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if(save!=null){
                return new CmsPageResult(CommonCode.SUCCESS,save);
            }
        }
        //修改失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //根据id删除页面
    public ResponseResult delete(String id){
        CmsPage cmsPage = this.getById(id);
        if(cmsPage!=null){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
    //页面静态化方法
    public String getPageHtml(String pageId){
        //获取数据模型
        Map model = getModelByPageId(pageId);
        if(model==null){
            //数据模型获取不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面的模板消息
        String template = getTemplateByPageId(pageId);
        if(template==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        return generateHtml(template, model);
    }
    //执行静态化
    public String generateHtml(String templateContent,Map model){
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        //在配置中设置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板内容
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
    //获取页面模板信息
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
        //根据文件id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        //打开一个下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建GridFsResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //从流中取数据
        String content = null;
        try {
            content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    //获取数据模型
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
    //页面发布
    public ResponseResult post(String pageId){
        //执行页面静态化
        String pageHtml = getPageHtml(pageId);
        //将页面静态化文件存储到GridFs中
        saveHtml(pageId,pageHtml);
        //向MQ发消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //向mq发送消息
    private void sendPostPage(String pageId){
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        }
        Map<String,String> msg=new HashMap<>();
        msg.put("pageId",pageId);
        String jsonString = JSON.toJSONString(msg);
        //站点id
        String siteId = cmsPage.getSiteId();
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
    }
    //保存html到GridFs
    private CmsPage saveHtml(String pageId,String htmlContent){
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage==null)
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        ObjectId objectId=null;
        //将htmlContent内容转成输入流
        try {
            InputStream inputStream = IOUtils.toInputStream(htmlContent,"utf-8");
            //将html文件内容保存到GridFs
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    //保存页面，有则更新，没有则添加
    public CmsPageResult save(CmsPage cmsPage) {
        CmsPage one = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(one!=null){
            return update(one.getPageId(),cmsPage);
        }
        return add(cmsPage);
    }

    //一键发布页面
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //将页面信息存储到cms_page中
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
        //拼接页面Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        String siteId = saveCmsPage.getSiteId();
        CmsSite cmsSite = findCmsSiteById(siteId);
        String pageUrl=cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+saveCmsPage.getPageWebPath()+cmsPage.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }
    //根据站点id查询站点信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite>  optional  = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return  optional.get();
        }
        return  null;
    }
}
