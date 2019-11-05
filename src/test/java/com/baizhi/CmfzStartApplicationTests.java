package com.baizhi;

import com.baizhi.dao.AdminDao;
import com.baizhi.entity.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CmfzStartApplicationTests {

    @Autowired
    private AdminDao adminDao;

    @Test
    void contextLoads() {
        Admin sky = adminDao.selectOne(new Admin().setUsername("sky"));
        System.out.println(sky);
    }

}
