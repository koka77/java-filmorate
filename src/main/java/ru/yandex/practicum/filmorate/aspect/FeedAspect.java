package ru.yandex.practicum.filmorate.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class FeedAspect {

    @After("execution(public * ru.yandex.practicum.filmorate.controller.*.*(..))")
    public void afterOperationAspect(JoinPoint joinPoint) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        log.info("был запущен метод : {}", methodName);
        log.info("с параметрами : {}", methodSignature.getParameterNames());
    }

/*    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*(..))")
    public void logFeed() {
        log.info("createUser: {}", 1);
    }*/
}
