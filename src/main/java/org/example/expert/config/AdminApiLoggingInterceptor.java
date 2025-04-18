package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class AdminApiLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // DELETE 메서드가 아니면 인터셉터 적용 안 함
        if (!request.getMethod().equalsIgnoreCase("DELETE")) {
            return true;
        }

        // 세션에서 사용자 역할 확인
        UserRole userRole = (UserRole) request.getSession().getAttribute("userRole");

        // 어드민이 아닌 유저가 접근을 시도한 경우
        if (!userRole.equals(UserRole.ADMIN)) {
            log.warn("비인가 접근 시도: {}", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "어드민 권한이 필요합니다.");
            return false;
        }

        // 세션에 저장된 사용자 ID를 꺼내옴
        String userId = (String) request.getSession().getAttribute("userId");

        // 유저 ID, 접속 시간, URI 로깅
        log.info("[어드민 API 접근] userId={}, time={}, uri={}",
                userId,
                System.currentTimeMillis(),
                request.getRequestURI()
        );

        return true;
    }

}