
package com.example.bt.app.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author : HauPV
 * Class dùng Aspect để thực hiện chức năng ghi log .
 */
@Slf4j
@Aspect
@Component
public class ControllerAspect {

    /*
     * Log ra thông tin của User hiện tại đang đăng nhập vào hệ thống
     * */
    @Before(value = "execution(public * com.example.bt.app.controller.*.*.*(..))")
    public void logUserDetails() {
        log.info("method : logUserDetails() , class : ControllerAspect");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("username : {}", authentication.getName());
        log.info("Role : {}", authentication.getAuthorities());
        log.info("details : {}", authentication.getDetails());
        log.info("Kết thúc method logUserDetails()");
    }

    /*
     * Log ra Controller nào đang được gọi đến -> sau khi return view name
     * */
    @AfterReturning(pointcut = "execution(public * com.example.bt.app.controller.*.*.*(..))")
    public void logControllerCalling(JoinPoint joinPoint) {
        log.info("method : logControllerCalling(JoinPoint joinPoint) , class : ControllerAspect");
        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("controllerName : {} ", controllerName);
        log.info("methodName : {} ", methodName);
        log.info("Kết thúc method logControllerCalling()");
    }
}
