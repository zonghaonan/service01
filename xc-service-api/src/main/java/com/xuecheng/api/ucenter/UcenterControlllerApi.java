package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Author zhn
 * @Date 2021/4/6 21:03
 * @Version 1.0
 */
@Api(value="用户中心",description = "用户中心管理")
public interface UcenterControlllerApi {
    @ApiOperation("根据用户账号查询用户信息")
    public XcUserExt getUserExt(String username);
}
