package org.pknu.weather.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public class ExecuteTimeAspect {

    @Around("org.pknu.weather.aop.pointcut.Pointcuts.doLogExecuteTime(pjp, memberId)")
    public void logExecuteTIme(ProceedingJoinPoint pjp, Long memberId) {

    }
}
