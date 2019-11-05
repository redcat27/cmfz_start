package com.baizhi.controller;

import com.baizhi.entity.Album;
import com.baizhi.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("album")
public class AlbumController {
    @Autowired
    private AlbumService albumService;


    /**
     * 查询所有专辑带分页
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("findAllByPage")
    public Map<String , Object> findAllByPage(Integer page, Integer rows){
        Map<String, Object> map = albumService.findAllByPage(page, rows);
        return map;
    }


    /**
     * 编辑一条数据
     * @param album
     * @param oper
     * @param request
     * @return
     */
    @RequestMapping("edit")
    public Map<String ,Object> edit(Album album, String oper, HttpServletRequest request){
        Map<String,Object> map = null;
        try {
            map = albumService.edit(album,oper,request);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 文件上传
     * @param cover
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("upload")
    public Map<String, Object> upload(MultipartFile cover,String id,HttpServletRequest request){
        Album album = new Album().setId(id);
        Map<String, Object> map = albumService.upload(album,cover,request);
        return map;
    }



}
