package org.pknu.weather.aop.pointcut;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut(value = "execution(* org.pknu.weather.controller.MainPageControllerV1.*(..)) && args(memberId)",
            argNames = "pjp,memberId")
    public void doCheckLocation(ProceedingJoinPoint pjp, Long memberId) {}

    @Pointcut(value ="execution(* org.pknu.weather.*(..)) && args(memberId)",
            argNames = "pjp,memberId")
    public void doLogExecuteTime(ProceedingJoinPoint pjp, Long memberId) {}
}
