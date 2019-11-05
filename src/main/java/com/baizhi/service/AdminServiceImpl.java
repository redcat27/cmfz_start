package com.baizhi.service;

import com.baizhi.dao.AdminDao;
import com.baizhi.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Transactional
@Service("adminService")
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminDao adminDao;


    /**
     * 后台管理员登录验证的功能
     * @param admin
     * @param code
     * @param request
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void checkLogin(Admin admin, String code, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("securityCode");
        if(sessionCode.equalsIgnoreCase(code)){
            //根据用户名查询一个用户
            Admin adminDB = adminDao.selectOne(admin);
            if(adminDB!=null){
                    session.setAttribute("admin",adminDB);//登录成功，打标记
            }else {
                throw new RuntimeException("用户名或者密码错误！");
            }
        }else {
            throw new RuntimeException("验证码错误！");
        }
    }
}
