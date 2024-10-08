package furniture.shop.configure;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AopConfig {

    @Around("execution(* furniture.shop..*Service.*(..)) || execution(* furniture.shop..*Controller.*(..))")
    public Object generateLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(">>> start : {} <<<", joinPoint.getSignature());

        Object result = joinPoint.proceed();

        log.info(">>> end : {}, return : {} <<<", joinPoint.getSignature(), result);

        return result;
    }

}
