package roomescape.presentation.resvoler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.business.model.Member;
import roomescape.business.service.AuthService;
import roomescape.business.vo.LoginMember;
import roomescape.presentation.support.CookieUtils;

@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String token = cookieUtils.getToken(request);
        Member authenticatedMember = authService.getAuthenticatedMember(token);

        return LoginMember.from(authenticatedMember);
    }
}
