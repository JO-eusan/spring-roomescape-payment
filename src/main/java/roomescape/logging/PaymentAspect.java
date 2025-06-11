package roomescape.logging;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PaymentAspect {

    @Pointcut("execution(* roomescape.infrastructure.payment.TossPaymentRestClient.requestConfirmation(..))")
    public void tossPaymentCall() {
    }

    @Around("tossPaymentCall()")
    public Object logTossPayment(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[TOSS REQUEST] {} CALL - args: {}", joinPoint.getSignature(),
            Arrays.toString(joinPoint.getArgs()));

        Object result = joinPoint.proceed();
        log.info("[TOSS RESPONSE] {} - result: {}", joinPoint.getSignature(), result);
        return result;
    }
}
