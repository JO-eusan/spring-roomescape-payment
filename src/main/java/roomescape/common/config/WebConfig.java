package roomescape.common.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.presentation.interceptor.AdminAuthInterceptor;
import roomescape.presentation.resvoler.LoginMemberArgumentResolver;
import roomescape.infrastructure.TossPaymentWithRestClient;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    private final AdminAuthInterceptor adminAuthInterceptor;

    public WebConfig(LoginMemberArgumentResolver loginMemberArgumentResolver,
                     AdminAuthInterceptor adminAuthInterceptor) {
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
        this.adminAuthInterceptor = adminAuthInterceptor;
    }

    @Bean
    public TossPaymentWithRestClient tossPaymentWithRestClient() {
        return new TossPaymentWithRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments").build()
        );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor).addPathPatterns("/admin/**");
    }
}
