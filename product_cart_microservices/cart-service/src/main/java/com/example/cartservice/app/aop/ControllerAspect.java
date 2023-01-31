package com.example.cartservice.app.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ControllerAspect {

    @Before("execution(public * com.example.cartservice.app.controller.*.*(..))")
    public void logController(JoinPoint joinPoint) {
        log.info("class {}", joinPoint.getTarget().getClass().getSimpleName());
        log.info("method {}", joinPoint.getSignature().getName());
    }

}
