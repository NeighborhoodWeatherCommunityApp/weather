package org.pknu.weather.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect  // only Aspect annotation
public class Pointcuts {

    @Pointcut("mainPageControllerV1Pointcut() || getMemberDefaultLocationPointcut()")
    public void doCheckLocationPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.controller.MainPageControllerV1.*(..))")
    public void mainPageControllerV1Pointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.controller.LocationControllerV1.getMemberDefaultLocation(..))")
    public void getMemberDefaultLocationPointcut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RestController) || !execution(* org.pknu.weather.controller.HealthCheckController.*.*(..))")
    public void controllerPointcut() {
    }

    @Pointcut("@target(org.springframework.stereotype.Service)")
    public void servicePointcut() {
    }

    @Pointcut("@annotation(org.springframework.stereotype.Repository)")
    public void repositoryPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.weather.repository.WeatherRedisRepository.*.*(..))")
    public void redisPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.weather.scheduler.*.*(..))")
    public void schedulerPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.dto.converter.*.*(..))")
    public void converterPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather..feignClient..*.*(..))")
    public void feignClientPointcut() {
    }

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalPointcut() {
    }

    @Pointcut("controllerPointcut() || servicePointcut() || feignClientPointcut() || repositoryPointcut()")
    public void devLoggingPointcut() {
    }

    @Pointcut("controllerPointcut()")
    public void prodLoggingPointcut() {
    }
}
