package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    @Autowired
    CmsPageRepository cmsPageRepository;

    //查询所有
    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }
    //分页查询
    @Test
    public void testFindPage(){
        int page=0;
        int size=10;
        Pageable pageable=PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println("test"+all);
    }
    //通过条件查询
    @Test
    public void testFindByExample(){
        int page=0;
        int size=10;
        Pageable pageable=PageRequest.of(page,size);
        CmsPage cmsPage = new CmsPage();
//        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
//        cmsPage.setTemplateId("5abf57965b05aa2ebcfce6d1");
        //设置别名
        cmsPage.setPageAliase("轮播");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //条件匹配器
        exampleMatcher=exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //定义Example
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }
    //修改
    @Test
    public void testUpdate(){
        //查询对象
        Optional<CmsPage> optional = cmsPageRepository.findById("5a7be667d019f14d90a1fb1c");
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            //设置要修改的值
            cmsPage.setPageAliase("分类导航");
            //修改
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }
    }
    @Test
    public void testFindByPageName(){
        CmsPage cmsPage = cmsPageRepository.findByPageAliase("轮播图");
        System.out.println(cmsPage);
    }

}
