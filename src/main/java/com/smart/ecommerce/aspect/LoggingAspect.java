package com.smart.ecommerce.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.smart.ecommerce.service..*(..))")
    public void allServicesMethods(){}

    @Before("allServicesMethods()")
    public void logBefore(JoinPoint joinPoint){
        System.out.println("{LOGGING} Log Before: " + joinPoint.getSignature());

    }

    @After("allServicesMethods()")
    public void logAfter(JoinPoint joinPoint){
        System.out.println("{LOGGING} Log after: " + joinPoint.getSignature());
    }

    @Around("allServicesMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        System.out.println("{LOGGING} Method: " + joinPoint.getSignature() + " Executed in " + (end - start) + "ms");
        return result;
    }

    @AfterThrowing(pointcut = "allServicesMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex){
        System.out.println("[ERROR] Exception in method: " + joinPoint.getSignature() +
                " -> " + ex.getMessage());
    }
}
