package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.UcenterControlllerApi;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zhn
 * @Date 2021/4/6 21:18
 * @Version 1.0
 */
@RestController
@RequestMapping("/ucenter")
public class UcenterController implements UcenterControlllerApi {
    @Autowired
    UserService userService;
    @Override
    @GetMapping("/getuserext")
    public XcUserExt getUserExt(@RequestParam("username") String username) {
        return userService.getUserExt(username);
    }
}
