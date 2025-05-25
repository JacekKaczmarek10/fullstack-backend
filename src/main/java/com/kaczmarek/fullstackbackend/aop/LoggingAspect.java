package com.kaczmarek.fullstackbackend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(Timed)")
    public Object logTimedMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Object result = joinPoint.proceed();
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        logger.info("[@Timed] {} wykonano w {} ms", joinPoint.getSignature(), durationMs);
        return result;
    }
}
