package com.baizhi.controller;

import com.baizhi.entity.Admin;
import com.baizhi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private AdminService adminService;


    @RequestMapping("login")
    @ResponseBody
    public Map<String , Object> login(Admin admin, String enCode, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        try {
            adminService.checkLogin(admin, enCode,request);
            map.put("success",true);
        } catch (Exception e) {
            map.put("success",false);
            map.put("message",e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

}
