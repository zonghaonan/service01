package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @Author zhn
 * @Date 2021/4/6 15:05
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    RestTemplate restTemplate;

    //远程请求spring security获取令牌
    @Test
    public void testClient() {
        //从euraka中获取认证服务的地址（因为spring security在认证服务中）
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //此地址就是http://ip:port
        URI uri = serviceInstance.getUri();
        //令牌申请地址：http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";
        //定义header
        LinkedMultiValueMap<String,String> header=new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic("XcWebApp", "XcWebApp");
        header.add("Authorization",httpBasic);
        //定义body
        LinkedMultiValueMap<String,String> body=new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","1234");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!=400&&response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        Map bodyMap = exchange.getBody();
        System.out.println(bodyMap);
    }
    //获取httpbasic串
    private String getHttpBasic(String clientId,String clientSecret){
        String string=clientId+":"+clientSecret;
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }
    @Test
    public void testPasswordEncoder(){
        //原始密码
        String password="11111";
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        for (int i = 0; i <10 ; i++) {
            String encode = bCryptPasswordEncoder.encode(password);
            System.out.println(encode);
            //校验
            boolean matches = bCryptPasswordEncoder.matches(password, encode);
            System.out.println(matches);
        }
    }
}
