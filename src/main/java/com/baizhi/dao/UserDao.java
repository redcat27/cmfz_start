package com.baizhi.dao;

import com.baizhi.entity.User;
import com.baizhi.entity.UserRegistCount;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserDao extends Mapper<User> {

    List<UserRegistCount> findRegistCountBySexAndYear(@Param("sex") String sex, @Param("year") String year);

}
