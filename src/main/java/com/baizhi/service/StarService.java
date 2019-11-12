package com.baizhi.service;

import com.baizhi.entity.Star;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface StarService {
    //分页查询所有
    Map<String , Object> findAllByPage(Integer page,Integer rows);

    Map<String, Object> edit(Star star, String oper,HttpServletRequest request);

    Map<String, Object> upload(Star star, MultipartFile photo, HttpServletRequest request);

    Map<String, Object> findAllOnlyStarName();

}
