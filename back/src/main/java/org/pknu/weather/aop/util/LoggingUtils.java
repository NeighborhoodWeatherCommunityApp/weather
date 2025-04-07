package org.pknu.weather.aop.util;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

@Slf4j
public class LoggingUtils {
    private static final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<StringBuilder> threadLocalSb = ThreadLocal.withInitial(StringBuilder::new);
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
        StringBuilder sb = threadLocalSb.get();

        Signature signature = pjp.getSignature();
        String fullName = signature.getDeclaringTypeName();
        String traceId = String.valueOf(Thread.currentThread().getId());
        String className = fullName.substring(fullName.lastIndexOf(".") + 1);
        String methodName = signature.getName();

        int currentDepth = depth.get();
        depth.set(currentDepth + 1);

        sb.append(String.format("\n[%s] %s%s%s.%s args=(%s)",
                traceId, DEPTH_PREFIX.repeat(currentDepth), START_PREFIX, className, methodName,
                Arrays.toString(args)
        ));
    }

    /**
     * 포인트컷 대상 메서드가 수행된 이후에 호출합니다. 포인트컷 대상 메서드가 종료되었음을 로깅합니다.
     *
     * @param pjp                 ProceedingJoinPoint
     * @param methodExecutionTime ExecutionTimerUtils.start() 의 return 값
     */
    public static void logAfterWithExecutionTime(ProceedingJoinPoint pjp, long methodExecutionTime) {
        StringBuilder sb = threadLocalSb.get();

        Signature signature = pjp.getSignature();
        String fullName = signature.getDeclaringTypeName();
        String traceId = String.valueOf(Thread.currentThread().getId());
        String className = fullName.substring(fullName.lastIndexOf(".") + 1);
        String methodName = signature.getName();

        int currentDepth = setCurrentDepthMinus();
        sb.append(String.format("\n[%s] %s%s%s.%s [time=%sms]",
                traceId, DEPTH_PREFIX.repeat(currentDepth), END_PREFIX, className, methodName, methodExecutionTime
        ));

        if (currentDepth == 0) {
            log.info(sb.toString());
            threadLocalSb.remove();
        }
    }

    /**
     * AOP 도중 에러가 발생하였을 때 호출합니다. 에러가 발생했음을 로깅합니다.
     *
     * @param pjp
     * @param ex
     */
    public static void logError(ProceedingJoinPoint pjp, Exception ex) {
        StringBuilder sb = threadLocalSb.get();

        Signature signature = pjp.getSignature();
        String fullName = signature.getDeclaringTypeName();
        String traceId = String.valueOf(Thread.currentThread().getId());
        String className = fullName.substring(fullName.lastIndexOf(".") + 1);
        String methodName = signature.getName();

        int currentDepth = depth.get();
        depth.remove();

        sb.append(String.format("\n[%s] %s%s %s.%s ",
                traceId, DEPTH_PREFIX.repeat(currentDepth), ERROR_PREFIX, className, methodName
        ));
        log.info(sb.toString());
        threadLocalSb.remove();
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
