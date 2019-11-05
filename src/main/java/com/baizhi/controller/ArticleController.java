package com.baizhi.controller;

import com.baizhi.entity.Article;
import com.baizhi.service.ArticleService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 分页查所有
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("findAllByPage")
    public Map<String ,Object> findAllByPage(Integer page, Integer rows){
        return articleService.findAllByPage(page,rows);
    }


    /**
     * 添加一个文章
     * @param article
     * @return
     */
    @RequestMapping("save")
    public Map<String ,Object> save(Article article){
        Map<String, Object> map = new HashMap<>();
        try {
            articleService.save(article);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 修改文章
     * @param article
     * @return
     */
    @RequestMapping("update")
    public Map<String ,Object> update(Article article){
        Map<String, Object> map = new HashMap<>();
        try {
            articleService.update(article);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 删除一个文章的方法
     * @param id
     * @return
     */
    @RequestMapping("delete")
    public Map<String ,Object> delete(String id){
        Map<String, Object> map = new HashMap<>();
        try {
            articleService.delete(id);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 文章的图片上传方法
     * @param articleImg
     * @param request
     * @return
     */
    @RequestMapping("upload")
    public Map<String ,Object> upload(MultipartFile articleImg, HttpServletRequest request){
        HashMap<String, Object> map = new HashMap<>();
        String realPath = request.getServletContext().getRealPath("/article/image");
        try {
            articleImg.transferTo(new File(realPath, articleImg.getOriginalFilename()));
            map.put("error",0);
            map.put("url","http://127.0.0.1:8989/start/article/image/"+articleImg.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            map.put("error",1);
        }
        return map;
    }


    @RequestMapping("imageSpace")
    public Map<String ,Object> imageSpace(HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        File file = new File(request.getServletContext().getRealPath("/article/image"));
        File[] files = file.listFiles();
        ArrayList<Object> list = new ArrayList<>();
        for (File img : files) {
            Map<String, Object> imgObject = new HashMap<>();
            imgObject.put("is_dir",false);
            imgObject.put("has_file",false);
            imgObject.put("filesize",img.length());
            imgObject.put("is_photo",true);
            imgObject.put("filetype", FilenameUtils.getExtension(img.getName()));
            imgObject.put("filename",img.getName());
            imgObject.put("datetime","2018-06-06 00:36:39");
            list.add(imgObject);
        }
        map.put("file_list",list);
        map.put("total_count",list.size());
        map.put("current_url","http://127.0.0.1:8989/start/article/image/");
        return map;
    }


}
