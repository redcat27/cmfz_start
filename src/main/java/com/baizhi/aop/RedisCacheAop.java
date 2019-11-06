package com.baizhi.aop;

import com.alibaba.fastjson.JSONObject;
import com.baizhi.annotation.RedisCache;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;

@Configuration
@Aspect
public class RedisCacheAop {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Around("execution(* com.baizhi.service.*Impl.find*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        //获取目标方法所在的 类的对象
        Object target = pjp.getTarget();
        //获取目标方法
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //获取目标方法参数
        Object[] args = pjp.getArgs();
        //获取目标方法对象
        Method method = methodSignature.getMethod();
        //判断目标方法是否有缓存注解
        if (method.isAnnotationPresent(RedisCache.class)) {
            //目标方法存在缓存注解
            //1.获取类的名字
            String className = target.getClass().getName();
            //2.获取方法名
            String methodName = method.getName();
            StringBuilder sb = new StringBuilder();
            sb.append(methodName).append("(");
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]);
                if (i == args.length) break;
                sb.append(",");
            }
            sb.append(")");
            String mapKey = sb.toString();
            //判断redis中是否有缓存数据
            Boolean key = stringRedisTemplate.opsForHash().hasKey(className, mapKey);
            if (key) {
                //表示redis中有缓存数据
                Object o = stringRedisTemplate.opsForHash().get(className, mapKey);
                return JSONObject.parse(o.toString());
            } else {
                //表示redis中没有缓存数据
                Object result = pjp.proceed();
                stringRedisTemplate.opsForHash().put(className, mapKey, JSONObject.toJSONString(result));
                return result;
            }

        } else {
            Object proceed = pjp.proceed();
            return proceed;
        }
    }


    @After("execution(* com.baizhi.service.*Impl.*(..)) && !execution(* com.baizhi.service.*Impl.find*(..))")
    public void after(JoinPoint joinPoint) {
        //获取目标方法所在的类
        Object target = joinPoint.getTarget();
        String className = target.getClass().getName();
        //判断数据库中是否存在这个类的缓存，如果存在则删除
        if (stringRedisTemplate.hasKey(className)) {
            stringRedisTemplate.delete(className);
        }
    }


}
