package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author zhn
 * @Date 2021/4/6 21:06
 * @Version 1.0
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser,String> {
    XcCompanyUser findByUserId(String userId);
}
