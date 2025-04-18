package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class AdminApiLoggingAspect {

    @Around("@annotation(org.example.expert.config.AdminApiLogging)")
    public Object logAdminApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // 현재 시각
        long start = System.currentTimeMillis();

        // 요청 정보
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String uri = request.getRequestURI();
        String userId = (String) request.getSession().getAttribute("userId");

        // RequestBody 직렬화
        String requestBody = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse))
                .map(arg -> {
                    try {
                        return new ObjectMapper().writeValueAsString(arg); // JSON 형식으로 변환
                    } catch (Exception e) {
                        return arg.toString();
                    }
                })
                .collect(Collectors.joining(", "));

        // 실제 비즈니스 로직 실행
        Object result = joinPoint.proceed();

        // ResponseBody 직렬화
        String responseBody = "";
        try {
            responseBody = new ObjectMapper().writeValueAsString(result); // JSON 형식으로 변환
        } catch (Exception e) {
            responseBody = result.toString();
        }

        // 로깅
        log.info("[어드민 API 접근] userId={}, time={}, uri={}, requestBody={}, responseBody={}",
                userId, start, uri, requestBody, responseBody);

        return result;
    }

}