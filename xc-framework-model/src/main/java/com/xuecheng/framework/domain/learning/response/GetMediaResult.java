package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author zhn
 * @Date 2021/3/28 18:28
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class GetMediaResult extends ResponseResult {
    String fileUrl;
    public GetMediaResult(ResultCode resultCode, String fileUrl){
        super(resultCode);
        this.fileUrl=fileUrl;
    }
}
