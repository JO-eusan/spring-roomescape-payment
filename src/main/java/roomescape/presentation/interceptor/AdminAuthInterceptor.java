package roomescape.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.business.service.AuthService;
import roomescape.dto.response.MemberResponse;
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
        MemberResponse memberResponse = authService.getMemberByToken(token);

        if (!memberResponse.role().isAdmin()) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
