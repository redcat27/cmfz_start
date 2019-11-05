package com.baizhi.service;

import com.baizhi.entity.Chapter;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ChapterService {
    //分页查询所有根据专辑id
    Map<String , Object> findAllByPageAndAlbumId(Chapter chapter, Integer page, Integer rows);

    Map<String, Object> edit(Chapter chapter, String oper, String albumId, HttpServletRequest request);

    Map<String, Object> upload(String id, MultipartFile name, String albumId, HttpServletRequest request);
}
