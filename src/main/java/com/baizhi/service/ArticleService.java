package com.baizhi.service;

import com.baizhi.entity.Article;

import java.util.List;
import java.util.Map;

public interface ArticleService {

    //分页查所有
    Map<String ,Object> findAllByPage(Integer page,Integer rows);

    //添加
    void save(Article article);

    //改
    void update(Article article);

    //删除
    void delete(String id);

    List<Article> search(String content);
}
