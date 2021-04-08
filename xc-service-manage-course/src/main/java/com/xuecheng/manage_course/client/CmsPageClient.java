package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author zhn
 * @Date 2021/3/28 14:32
 * @Version 1.0
 */
@FeignClient(value = "XC-SERVICE-MANAGE-CMS")  //指定远程调用的服务名
public interface CmsPageClient {
    //根据页面id查询页面信息,远程调用cms请求数据
    @GetMapping("/cms/page/get/{id}")  //用GetMapping标识远程调用的http的方法类型
    public CmsPageResult findById(@PathVariable("id") String id);
    //添加页面，用于预览
    @PostMapping("/cms/page/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage);
    //一键发布
    @PostMapping("/cms/page/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);
}
