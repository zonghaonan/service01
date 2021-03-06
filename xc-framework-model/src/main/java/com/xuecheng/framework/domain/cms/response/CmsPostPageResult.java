package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author zhn
 * @Date 2021/3/29 8:54
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {
    String  pageUrl;
    public  CmsPostPageResult(ResultCode resultCode, String pageUrl) {
        super(resultCode);
        this.pageUrl  =  pageUrl;
    }
}
