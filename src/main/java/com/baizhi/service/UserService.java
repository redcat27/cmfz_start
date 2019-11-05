package com.baizhi.service;

import com.baizhi.entity.User;
import com.baizhi.entity.UserRegistCount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserService {
    //根据明星id查询所有的用户
    Map<String , Object> findAllByStarIDAndPage(Integer page,Integer rows,String starId);

    HashMap<String, Object> findAllByPage(Integer page, Integer rows);

    List<User> findAll();

    Long[] findRegistCountBySexAndYear(String sex,String year);
}
