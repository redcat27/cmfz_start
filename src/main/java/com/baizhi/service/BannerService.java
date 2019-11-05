package com.baizhi.service;

import com.baizhi.entity.Banner;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BannerService {
    //查所有带分页
    Map<String,Object> findAllByPage(Integer start, Integer rows);
    //编辑轮播图
    String edit(Banner banner, String oper, HttpServletRequest request);

    public Map<String,Object> uploade(Banner banner,MultipartFile cover,HttpServletRequest request);


}
