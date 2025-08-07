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

    @Pointcut("execution(* org.pknu.weather.controller..*.*(..))" +
            " && !execution(* org.pknu.weather.controller.HealthCheckController.*(..))")
    public void controllerPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.service.*.*(..))")
    public void servicePointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.repository.*.*(..))")
    public void repositoryPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.weather.repository.WeatherRedisRepository.*(..))")
    public void redisPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.dto.converter.*.*(..))")
    public void converterPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.feignClient..*.*(..))")
    public void feignClientPointcut() {
    }

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalPointcut() {
    }

    @Pointcut("controllerPointcut() || servicePointcut() || feignClientPointcut()")
    public void devLoggingPointcut() {
    }

    @Pointcut("controllerPointcut()")
    public void prodLoggingPointcut() {
    }
}
