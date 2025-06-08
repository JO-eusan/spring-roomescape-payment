package roomescape.logging;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

    @Pointcut("execution(* roomescape..presentation..controller..*(..))")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        Object[] args = joinPoint.getArgs();

        log.info("[REQUEST] {} {} - {}.{} - args: {}", method, uri, className,
            joinPoint.getSignature().getName(), Arrays.toString(args));

        Object result = joinPoint.proceed();

        log.info("[RESPONSE] {} {} - result: {}", method, uri, result);
        return result;
    }
}
