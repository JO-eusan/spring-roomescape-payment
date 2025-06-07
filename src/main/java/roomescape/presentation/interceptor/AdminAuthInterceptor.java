package roomescape.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.application.service.AuthService;
import roomescape.dto.response.MemberResponseDto;
import roomescape.presentation.support.CookieUtils;

@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        if (!cookieUtils.containsCookieForToken(request)) {
            response.setStatus(401);
            return false;
        }

        String token = cookieUtils.getToken(request);
        MemberResponseDto memberResponseDto = authService.getMemberByToken(token);

        if (!memberResponseDto.role().isAdmin()) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
