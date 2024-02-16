package com.sky.aspect;

import com.sky.anno.AutoFill;
import com.sky.context.ThreadLocalUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {
    @Pointcut("@annotation(com.sky.anno.AutoFill)")
    public void pt(){};

    @Before("pt()")
    public void autoFill(JoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
        //1. 获取参数对象，获取对象中要调用的方法（setCreateUser setCreateTime setUpdateTime setUpdateUser ）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length < 1) { //判断有没有参数
            return;
        }
        //获取参数中的目标对象
        Object obj = args[0];

        //2. 调用 setUpdateTime setUpdateUser 方法进行数据填充
        Method setUpdateTime = getMethod(obj, "setUpdateTime", LocalDateTime.class);
        Method setUpdateUser = getMethod(obj, "setUpdateUser", Long.class);
        if (setUpdateTime != null) {
            //反射调用方法，填充数据
            setUpdateTime.invoke(obj, LocalDateTime.now());
        }
        if (setUpdateUser != null) {
            setUpdateUser.invoke(obj, ThreadLocalUtil.getCurrentId());
        }
        //3. 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        //4. 判断注解的value属性值，是不是insert ，如果是，还需要调用setCreateUser setCreateTime进行属性赋值
        if (annotation.value().equals("insert")) {
            Method setCreateTime = getMethod(obj, "setCreateTime", LocalDateTime.class);
            Method setCreateUser = getMethod(obj, "setCreateUser", Long.class);
            if (setCreateTime != null) {
                //反射调用方法，填充数据
                setCreateTime.invoke(obj, LocalDateTime.now());
            }
            if (setCreateUser != null) {
                setCreateUser.invoke(obj, ThreadLocalUtil.getCurrentId());
            }
        }


    }

    //反射获取方法
    private Method getMethod(Object obj, String methodName, Class... args) {
        try {
            //3.1 反射获取方法: 指定方法名+参数类型，因为类中可能有重载方法
            Method method = obj.getClass().getDeclaredMethod(methodName, args);
            return method;
        } catch (NoSuchMethodException e) {
            //抛出异常说明方法不存在
            return null;
        }
    }
}
