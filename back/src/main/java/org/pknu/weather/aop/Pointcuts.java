package org.pknu.weather.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect  // only Aspect annotation
public class Pointcuts {

    @Pointcut("execution(* org.pknu.weather.controller.MainPageControllerV1.*(..))")
    public void doCheckLocationPointcut() {}

    @Pointcut("execution(* org.pknu.weather.controller.*.*(..))")
    public void controllerPointcut() {}

    @Pointcut("execution(* org.pknu.weather.service.*.*(..))")
    public void servicePointcut() {}

    @Pointcut("execution(* org.pknu.weather.repository.*.*(..))")
    public void repositoryPointcut() {}

    @Pointcut("execution(* org.pknu.weather.dto.converter.*.*(..))")
    public void converterPointcut() {}


    // 포인트컷 대상 : @Transactional 이 붙은 모든 메서드
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalPointcut(){}

    @Pointcut("controllerPointcut() || servicePointcut() || repositoryPointcut() || converterPointcut()")
    public void commonLoggingPointcut(){}
}
