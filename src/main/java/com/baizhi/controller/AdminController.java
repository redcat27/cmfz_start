package com.baizhi.controller;

import com.baizhi.entity.Admin;
import com.baizhi.service.AdminService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private AdminService adminService;


    /**
     * 登录验证，使用shiro验证
     *
     * @param admin
     * @param enCode
     * @param request
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public Map<String , Object> login(Admin admin, String enCode, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("securityCode");
        Map<String, Object> map = new HashMap<>();
        //判断验证码是否正确，在进行身份你验证
        try {
            if (sessionCode.equalsIgnoreCase(enCode)) {
                //获取令牌，将用户名密码，存到令牌中
                UsernamePasswordToken token = new UsernamePasswordToken(admin.getUsername(), admin.getPassword());
                //获取主体
                Subject subject = SecurityUtils.getSubject();
                //主体调用shiro封装的login方法，去验证用户名和密码
                subject.login(token);
                map.put("success", true);
            } else {
                throw new RuntimeException("验证码错误");
            }
        } catch (AuthenticationException e) {
            map.put("success",false);
            map.put("message",e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 安全退出的方法
     */
    @RequestMapping("logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/back/login.jsp";
    }

}
