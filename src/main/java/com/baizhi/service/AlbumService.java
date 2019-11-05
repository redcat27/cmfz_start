package com.baizhi.service;

import com.baizhi.entity.Album;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AlbumService {
    //展示所有带分页
    Map<String,Object> findAllByPage(Integer page, Integer rows);

    //编辑一个专辑
    Map<String, Object> edit(Album album, String oper, HttpServletRequest request);

    Map<String, Object> upload(Album album, MultipartFile cover, HttpServletRequest request);
}
