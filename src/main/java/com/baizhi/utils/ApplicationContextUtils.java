package com.baizhi.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component //springboot实例化这个类
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext context  ;

    //springboot会在创建后工厂之后自动执行setApplicationContext 将创建好的工厂作为该方法参数传递给该方法
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            context = applicationContext;
    }

    //根据id获取工厂中指定对象的方法
    public static Object getBean(String id){
        return context.getBean(id);
    }

    //根据类型获取工厂中指定对象的方法
    public static Object getBean(Class clazz){
        return context.getBean(clazz);
    }

    //根据类型和id获取工厂中指定对象的方法
    public static Object getBean(String id,Class clazz){
        return context.getBean(id,clazz);
    }

}
