package org.pknu.weather.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect  // only Aspect annotation
public class Pointcuts {


    @Pointcut("within(org.pknu.weather.infra.mornitoring.HealthCheckController)")
    public void healthCheckController() {}

    // Controller: @RestController 붙은 클래스 전부, 단 HealthCheckController는 제외
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && !healthCheckController()")
    public void controllerPointcut() {}

    // Service: service 패키지 + @Service (둘 다 조건)
    @Pointcut("within(org.pknu.weather..service..*) || @within(org.springframework.stereotype.Service)")
    public void servicePointcut() {}

    // Repository: repository 패키지 + @Repository
    @Pointcut("within(org.pknu.weather..repository..*) || @within(org.springframework.stereotype.Repository)")
    public void repositoryPointcut() {}

    @Pointcut("execution(* org.pknu.weather.weather.repository.WeatherRedisRepository.*.*(..))")
    public void redisPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather.weather.scheduler.*.*(..))")
    public void schedulerPointcut() {
    }

    @Pointcut("within(org.pknu.weather..converter..*)")
    public void converterPointcut() {
    }

    @Pointcut("execution(* org.pknu.weather..feignClient..*.*(..))")
    public void feignClientPointcut() {
    }

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalPointcut() {
    }

    @Pointcut("controllerPointcut() || servicePointcut() || feignClientPointcut() || repositoryPointcut() || converterPointcut()")
    public void devLoggingPointcut() {
    }

    @Pointcut("controllerPointcut()")
    public void prodLoggingPointcut() {
    }
}
