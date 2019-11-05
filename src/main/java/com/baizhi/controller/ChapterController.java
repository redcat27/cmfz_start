package com.baizhi.controller;

import com.baizhi.entity.Chapter;
import com.baizhi.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("chapter")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;


    /**
     * 分页查询所有音频根据专辑id
     * @param id
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("findAllByPageAndChapterId")
    public Map<String , Object> findAllByPageAndChapterId(String id,Integer page, Integer rows){
        Chapter chapter = new Chapter().setAlbumId(id);
        Map<String, Object> map = chapterService.findAllByPageAndAlbumId(chapter, page, rows);
        return map;
    }


    /**
     * 编辑一个章节（歌曲）的音频
     * @param chapter
     * @param oper
     * @param albumId
     * @param request
     * @return
     */
    @RequestMapping("edit")
    public Map<String, Object> edit(Chapter chapter, String oper, String albumId, HttpServletRequest request){
        Map<String, Object> map = null;
        try {
            map = chapterService.edit(chapter, oper, albumId, request);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 专辑所属的章节（歌曲）的音频文件上传
     * @param id
     * @param albumId
     * @param name
     * @param request
     * @return
     */
    @RequestMapping("upload")
    public Map<String, Object> upload(String id, String albumId, MultipartFile name, HttpServletRequest request){
        Map<String, Object> map = chapterService.upload(id,name, albumId, request);
        return map;
    }


}
