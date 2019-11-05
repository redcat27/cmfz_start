package com.baizhi.service;

import com.baizhi.entity.Admin;

import javax.servlet.http.HttpServletRequest;

public interface AdminService {
    //登录检查
    void checkLogin(Admin admin, String code, HttpServletRequest request);
}
