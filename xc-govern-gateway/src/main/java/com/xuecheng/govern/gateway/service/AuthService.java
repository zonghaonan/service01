package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhn
 * @Date 2021/4/7 13:26
 * @Version 1.0
 */
@Service
public class AuthService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    //从头取出jwt令牌
    public String getJwFromHeader(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)||!authorization.startsWith("Bearer ")){
            return null;
        }
        return authorization.substring(7);
    }
    //从cookie中取出token
    //取出cookie中的用户身份令牌
    public String getTokenFormCookie(HttpServletRequest request){
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map!=null&&map.get("uid")!=null){
            String uid = map.get("uid");
            return uid;
        }
        return null;
    }
    //查询令牌的有效期
    public long getExpire(String access_token){
        String key="user_token:"+access_token;
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire;
    }

}
