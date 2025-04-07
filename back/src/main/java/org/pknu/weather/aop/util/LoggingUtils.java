package org.pknu.weather.aop.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

@Slf4j
public class LoggingUtils {
    private static final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
    private static final String START_PREFIX = "|-->";
    private static final String END_PREFIX = "|<--";
    private static final String ERROR_PREFIX = "|<X-";
    private static final String DEPTH_PREFIX = "|  ";

    /**
     * 포인트컷 대상 메서드가 수행되기 전에 호출합니다. 포인트컷 대상 메서드가 시작되었음을 로깅합니다.
     *
     * @param pjp  ProceedingJoinPoint
     * @param args 필요하다면 파라미터도 넘길 수 있다.
     */
    public static void logBefore(ProceedingJoinPoint pjp, Object[] args) {
        StringBuilder sb = new StringBuilder();

        Signature signature = pjp.getSignature();
        String fullName = signature.getDeclaringTypeName();
        String traceId = String.valueOf(Thread.currentThread().getId());
        String className = fullName.substring(fullName.lastIndexOf(".") + 1);
        String methodName = signature.getName();

        int currentDepth = depth.get();
        sb.append(DEPTH_PREFIX.repeat(currentDepth)).append(START_PREFIX);
        depth.set(currentDepth + 1);

        log.info("[{}] {}{}.{} args=({})", traceId, sb, className, methodName, args);
    }

    /**
     * 포인트컷 대상 메서드가 수행된 이후에 호출합니다. 포인트컷 대상 메서드가 종료되었음을 로깅합니다.
     *
     * @param pjp                 ProceedingJoinPoint
     * @param methodExecutionTime ExecutionTimerUtils.start() 의 return 값
     */
    public static void logAfterWithExecutionTime(ProceedingJoinPoint pjp, long methodExecutionTime) {
        StringBuilder sb = new StringBuilder();

        Signature signature = pjp.getSignature();
        String fullName = signature.getDeclaringTypeName();
        String traceId = String.valueOf(Thread.currentThread().getId());
        String className = fullName.substring(fullName.lastIndexOf(".") + 1);
        String methodName = signature.getName();

        int currentDepth = setCurrentDepthMinus();
        sb.append(DEPTH_PREFIX.repeat(currentDepth)).append(END_PREFIX);

        log.info("[{}] {}{}.{} [time={}ms]", traceId, sb, className, methodName, methodExecutionTime);
    }

    /**
     * 포인트컷 대상 메서드가 수행된 이후에 호출합니다. 포인트컷 대상 메서드가 종료되었음을 로깅합니다.
     *
     * @param pjp ProceedingJoinPoint
     */
    public static void logAfter(ProceedingJoinPoint pjp) {
        StringBuilder sb = new StringBuilder();

        Signature signature = pjp.getSignature();
        String fullName = signature.getDeclaringTypeName();
        String traceId = String.valueOf(Thread.currentThread().getId());
        String className = fullName.substring(fullName.lastIndexOf(".") + 1);
        String methodName = signature.getName();

        int currentDepth = setCurrentDepthMinus();
        sb.append(DEPTH_PREFIX.repeat(currentDepth)).append(END_PREFIX);

        log.info("[{}] {}{}.{}", traceId, sb, className, methodName);
    }

    /**
     * AOP 도중 에러가 발생하였을 때 호출합니다. 에러가 발생했음을 로깅합니다.
     *
     * @param pjp
     * @param ex
     */
    public static void logError(ProceedingJoinPoint pjp, Exception ex) {
        StringBuilder sb = new StringBuilder();

        Signature signature = pjp.getSignature();
        String fullName = signature.getDeclaringTypeName();
        String traceId = String.valueOf(Thread.currentThread().getId());
        String className = fullName.substring(fullName.lastIndexOf(".") + 1);
        String methodName = signature.getName();

        int currentDepth = depth.get();
        sb.append(DEPTH_PREFIX.repeat(currentDepth)).append(ERROR_PREFIX);
        depth.remove();

        log.info("[{}] {}{}.{}", traceId, sb, className, methodName);
    }

    private static int setCurrentDepthMinus() {
        int currentDepth = depth.get();

        if (currentDepth == 0) {
            depth.remove();
        } else {
            depth.set(currentDepth - 1);
            return currentDepth - 1;
        }

        return currentDepth;
    }
}
