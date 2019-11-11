package com.baizhi.conf;

import com.baizhi.realm.MyRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration  //表示当前类是配置类
public class ShiroFilter {


    /**
     * 将shiro的拦截器对象交给工厂创建
     * 必须设置SecurityManager属性值
     *
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager) {
        //1.创建一个shiroFilterFactoryBean对象
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //必须设置的属性--安全管理器对象，给其赋值
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //设置拦截规则   anon  authc
        HashMap<String, String> map = new HashMap<>();
        //拦截的资源--所有
        map.put("/**", "authc");
        //设置不拦截的资源--登录访问的controller
        map.put("/admin/login", "anon");
        map.put("/security/code", "anon");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        //制定登录页面的url
        shiroFilterFactoryBean.setLoginUrl("/login/login.jsp");
        return shiroFilterFactoryBean;
    }


    /**
     * 将安全管理器对象交给工厂管理，并设置属性走自定义的realm和加入缓存
     *
     * @param myRealm
     * @param cacheManager
     * @return
     */
    @Bean
    public SecurityManager getSecurityManager(MyRealm myRealm, CacheManager cacheManager) {
        //创建一个安全管理器
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //给安全管理器设置自定义的realm，不走shiro默认的realm
        securityManager.setRealm(myRealm);
        //给安全管理器加入缓存功能
        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }


    //自定义的realm对象交给工厂管理
    @Bean
    public MyRealm getMyRealm(HashedCredentialsMatcher hashedCredentialsMatcher) {
        MyRealm myRealm = new MyRealm();
        //给自定义的realm设置自定义的散列凭证匹配器，用于验证密码的方法
        myRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return myRealm;
    }


    /**
     * 将凭证匹配器交给工厂管理
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher getHashedCredentialsMatcher() {
        //获取凭证匹配器
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //设置凭证匹配器规则
        matcher.setHashAlgorithmName("MD5");
        matcher.setHashIterations(1024);
        return matcher;
    }


    /**
     * shiro的缓存对象交给工厂处理
     *
     * @return
     */
    @Bean
    public CacheManager getCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        return ehCacheManager;
    }


}
