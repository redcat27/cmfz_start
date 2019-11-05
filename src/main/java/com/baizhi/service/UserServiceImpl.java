package com.baizhi.service;

import com.baizhi.dao.UserDao;
import com.baizhi.entity.User;
import com.baizhi.entity.UserRegistCount;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Map<String, Object> findAllByStarIDAndPage(Integer page, Integer rows, String starId) {
        User user = new User().setStarId(starId);
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        List<User> users = userDao.selectByRowBounds(user, rowBounds);
        int count = userDao.selectCount(user);
        Map<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("rows",users);
        map.put("total",count%rows==0?count/rows:count/rows+1);
        map.put("records",count);
        return map;
    }


    /**
     * 分页查所有用户
     * @param page
     * @param rows
     * @return
     */
    @Override
    public HashMap<String, Object> findAllByPage(Integer page, Integer rows) {
        User user = new User();
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        List<User> users = userDao.selectByRowBounds(user, rowBounds);
        int count = userDao.selectCount(user);
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("rows",users);
        map.put("total",count%rows==0?count/rows:count/rows+1);
        map.put("records",count);
        return map;
    }


    /**
     * 获取所有的用户
     * @return
     */
    @Override
    public List<User> findAll() {
        List<User> users = userDao.selectAll();
        return users;
    }


    /**
     * 根据用户性别和想要查询的年份，查询各个月份下注册的用户数
     * @param sex
     * @param year
     * @return
     */
    @Override
    public Long[] findRegistCountBySexAndYear(String sex, String year) {
        //获取数据
        List<UserRegistCount> list = userDao.findRegistCountBySexAndYear(sex, year);
        Long[] m = {0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L};
        for (UserRegistCount u : list) {
            for (int i = 1; i<=12 ;i++){
                if (i==u.getMonth()){
                    m[i-1] = u.getCount();
                }
            }
        }
        return m;
    }

}
