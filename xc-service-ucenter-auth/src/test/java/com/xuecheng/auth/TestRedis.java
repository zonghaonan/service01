package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhn
 * @Date 2021/4/6 14:10
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    public void testRedis(){
        String key="user_token:4a21e620-ecf6-4ea3-9426-debf77f12366";
        Map<String,String> map=new HashMap<>();
        map.put("jwt","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTYxNzcyMTAwMSwianRpIjoiNGEyMWU2MjAtZWNmNi00ZWEzLTk0MjYtZGViZjc3ZjEyMzY2IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.eMd-epGtLlTDsUhaewB4u6Q7uh2Bh9p6JHSAmSRl29uTz1SGs24c4lwZythyvzdFBX05wLecKFLqMK1ShIQy5nidQF8DGs8frveYKea-pKwsDVttzhtCuUhDHt6XIjkvV9UxP_akePtAzNyeXf1B4mm3DVqBJC1k_LvE3mwX1s9f8WPz2My84QhMVVt4SxMsPY3m2po5tQdpFu8aSWIjiVWIm1V9sT21xweOab5Nug7y-olQLtohBtxLAMkrjTI8UGSuIQOiv_Eevw0flh3FkAo8IHuyjKIC1NhwCK1lCs5d5COi06kQacFswlv2acFg9Yj294yXuK9i2GflfJ_Slg");
        map.put("refresh_token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiI0YTIxZTYyMC1lY2Y2LTRlYTMtOTQyNi1kZWJmNzdmMTIzNjYiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTYxNzcyMTAwMSwianRpIjoiMGIxYmU4YTktY2E4My00MzI1LWFiZGMtYWE0ZTVmNmY0NGMxIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.fGzpMg4eSa16h9XvoqsxYj0Dxiu7HpL8ZVLH8jYKVNcrhjsqFHG1b82RlUkHylcFK63FX7IMoF2OIsMW7mn9UNQ7PW3sknqwRuBrQASp66I2PtZlNPXoGbnze8lkl7_WlRLJWbxFOp5keWQjCil65q2kOEfVgCwrZm5aNOv_hF2VlmgbvjuerrSoi2bzd8q8FEFd3miNSf9fRSQh1H7GYoE8wJm5eLUB-_HAKeDYiNrJU05PDwBIdBA3c-mar2BJAS9SIvSdHlrybXL49bm__8kkZBJyucJuRo-T20fog8CY4hoqftQx9QPsZxTUAHHclL3OqK-JL99RuXCjY_cBaQ");
        String jsonString = JSON.toJSONString(map);
        //校验key是否存在，如果不存在则返回-2
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        System.out.println(expire);
        //存储数据
        stringRedisTemplate.boundValueOps(key).set(jsonString,30, TimeUnit.SECONDS);
        //获取数据
        String string = stringRedisTemplate.opsForValue().get(key);
        System.out.println(string);
    }
}
