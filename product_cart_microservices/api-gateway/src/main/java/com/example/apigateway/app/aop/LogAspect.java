package com.example.apigateway.app.aop;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

    @SneakyThrows
    @AfterReturning(pointcut = "execution(public * com.example.apigateway.app.controller.*.*(..))")
    public void invalidateSession(JoinPoint joinPoint) {
        log.info("class {}", joinPoint.getTarget().getClass().getSimpleName());
        log.info("method : {}", joinPoint.getSignature().getName());
        //        httpSession.invalidate();
    }
}
