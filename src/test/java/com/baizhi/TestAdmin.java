package com.baizhi;

import com.baizhi.controller.UserController;
import com.baizhi.dao.AdminDao;
import com.baizhi.service.BannerService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.util.Map;
import java.util.Set;

@SpringBootTest
public class TestAdmin {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AdminDao adminDao;
    @Autowired
    private BannerService bannerService;
    @Autowired
    private UserController userController;

    @Test
    public void test(){
//        Admin sky = adminDao.selectOne(new Admin().setUsername("sky"));
//        System.out.println(sky);

//        redisTemplate.opsForValue().set("name","zhangsan");
//        Object name = redisTemplate.opsForValue().get("name");
//        System.out.println(name);

//        stringRedisTemplate.opsForValue().set("name","lisi");
//        String name = stringRedisTemplate.opsForValue().get("name");
//        System.out.println(name);

        stringRedisTemplate.opsForValue().set("xxxx", "xxxx");

    }

   /* @Test
    public void m(){
        List<Banner> allByPage = bannerService.findAllByPage(1, 2);
        allByPage.forEach(a -> System.out.println(a));
    }*/

   @Test
   public void m(){
       Map<String, Object> allByPage = bannerService.findAllByPage(1, 2);
       System.out.println(allByPage);
   }


    /**
     * 测试redis
     */
   @Test
    public void m1(){
       /*redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
       redisTemplate.opsForValue().set("456","123123");*/

       stringRedisTemplate.opsForValue().set("789","34856");
   }

   @Test
   public void m3(){
       redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
       Set keys = redisTemplate.keys("*");
      /* System.out.println(keys);
       for (Object key : keys) {
           System.out.println(key);
       }*/
       String code = (String) redisTemplate.opsForValue().get("code");
       System.out.println(code);

   }


   @Test
   public void testSendMessage(){
       //String [] s = {"13331025260","15261869980"};
       //UserController userController = new UserController();
       try {
//           Integer random = (int) (((Math.random() * 9) + 1) * 100000);
//           String str = random.toString();
//           System.out.println(str);
           userController.sendPhoneMessageCode("15010102217");
       } catch (Exception e) {
           e.printStackTrace();
       }
   }




   @Test
    public void testRandom(){
       Integer random = (int) (((Math.random() * 9) + 1) * 100000);
       System.out.println(random);
       String s = random.toString();
       System.out.println("转成的字符串"+s);
   }


    @Test
    public void testMD5code() {
        Md5Hash sky = new Md5Hash("123", "abcd", 1024);
        String s = sky.toHex();
        System.out.println(s);
    }

}
