package roomescape.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.business.service.AuthService;
import roomescape.presentation.interceptor.AdminAuthInterceptor;
import roomescape.presentation.resvoler.LoginMemberArgumentResolver;
import roomescape.presentation.support.CookieUtils;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(authService, cookieUtils));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminAuthInterceptor(authService, cookieUtils))
            .addPathPatterns("/admin/**");
    }
}
