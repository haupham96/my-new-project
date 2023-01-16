package com.example.oath2springsecurity5.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Aspect
@Component
@Slf4j
public class RestControllerAspect {

    @Autowired
    private HttpSession httpSession;

    @AfterReturning(pointcut = "execution(public * com.example.oath2springsecurity5.controller.*.*(..))")
    public void handlerStatelessSession(JoinPoint joinPoint) {
        log.info("class : {}",joinPoint.getTarget().getClass().getSimpleName());
        log.info("method : {}",joinPoint.getSignature().getName());
        httpSession.invalidate();
        log.info("cleared session");
    }
}
