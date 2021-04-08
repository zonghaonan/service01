package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author zhn
 * @Date 2021/4/7 13:12
 * @Version 1.0
 */
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    AuthService authService;
    //过滤器的类型
    @Override
    public String filterType() {
        /*
             pre：请求在被路由之前执行
             routing：在路由请求时调用
             post：在routing和errror过滤器之后调用
             error：处理请求时发生错误调用
         */
        return "pre";
    }

    //过滤器序号，越小越被优先执行
    @Override
    public int filterOrder() {
        return 0;
    }

    //
    @Override
    public boolean shouldFilter() {
        //返回true表示执行此过滤器
        return true;
    }
    //过虑器的内容
    //测试的需求：过虑所有请求，判断头部信息是否有Authorization，如果没有则拒绝访问，否则转发到微服务。
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //得到request
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();
        //取出cookie中的用户身份令牌
        String tokenFormCookie = authService.getTokenFormCookie(request);
        String jwFromHeader = authService.getJwFromHeader(request);
        long expire = authService.getExpire(tokenFormCookie);
        if(StringUtils.isEmpty(tokenFormCookie)||StringUtils.isEmpty(jwFromHeader)||expire<0){
            //拒绝访问
            access_denied(requestContext,response);
        }
        return null;
    }
    //拒绝访问
    private void access_denied(RequestContext requestContext,HttpServletResponse response){
        //拒绝访问
        requestContext.setSendZuulResponse(false);
        //设置响应代码
        requestContext.setResponseStatusCode(200);
        //构建响应的信息
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String jsonString = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(jsonString);
        response.setContentType("application/json;charset=utf-8");
    }

}
