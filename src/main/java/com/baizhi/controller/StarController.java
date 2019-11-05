package com.baizhi.controller;

import com.baizhi.entity.Star;
import com.baizhi.service.StarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("star")
public class StarController {
    @Autowired
    private StarService starService;


    /**
     * 查询所有的方法带分页
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("findAllByPage")
    public Map<String, Object> findAllByPage(Integer page, Integer rows){
        Map<String, Object> map = starService.findAllByPage(page, rows);
        return map;
    }

    /**
     * 编辑一个明星的方法
     * @param star
     * @param oper
     * @param request
     * @return
     */
    @RequestMapping("edit")
    public Map<String ,Object> edit(Star star,String oper,HttpServletRequest request){
        Map<String, Object> map = null;
        try {
            map = starService.edit(star,oper,request);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 上传明星图片
     * @param id
     * @param photo
     * @param request
     * @return
     */
    @RequestMapping("upload")
    public Map<String, Object> upload(String id, MultipartFile photo, HttpServletRequest request){
        Star star = new Star().setId(id);
        Map<String, Object> map = starService.upload(star,photo,request);
        return map;
    }


    /**
     * 查询所有的歌手或者叫作者，用于前台的下拉框展示
     * @return
     */
    @RequestMapping("findAllOnlyStarName")
    public Map<String,Object> findAllOnlyStarName(){
        Map<String,Object> map = starService.findAllOnlyStarName();
        return map;
    }

}
