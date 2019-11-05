package com.baizhi.controller;

import com.baizhi.entity.Banner;
import com.baizhi.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("banner")
public class BannerController {
    @Autowired
    private BannerService bannerService;


    /**
     * 分页查询所有轮播图
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("findAllByPager")
    @ResponseBody
    public Map<String , Object> findAllByPager(Integer page, Integer rows){
        Map<String, Object> map = bannerService.findAllByPage(page, rows);
        return map;
    }


    @RequestMapping("edit")
    @ResponseBody
    public Map<String,Object> edit(Banner banner,String oper,HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        try {
            String id = bannerService.edit(banner, oper, request);
            map.put("status",true);
            map.put("message",id);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    @RequestMapping("uploade")
    @ResponseBody
    public Map<String ,Object> uploade(String id, MultipartFile cover,HttpServletRequest request){
        Banner banner = new Banner().setId(id);
        Map<String, Object> map = bannerService.uploade(banner, cover, request);
        return map;
    }

}
