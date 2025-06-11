package roomescape.common.config;

import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.handler.PaymentErrorHandler;
import roomescape.infrastructure.payment.TossPaymentRestClient;

@Configuration
public class PaymentConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEME = "Basic ";

    @Value("${security.toss.payment.secret-key}")
    private String secretKey;

    @Bean
    public TossPaymentRestClient tossPaymentWithRestClient(RestClient.Builder builder) {
        return new TossPaymentRestClient(builder
            .baseUrl("https://api.tosspayments.com/v1/payments")
            .defaultHeader(AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME + encodeSecretKey())
            .defaultStatusHandler(new PaymentErrorHandler())
            .requestFactory(createRequestFactory())
            .build());
    }

    private static SimpleClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(2));
        return requestFactory;
    }

    private String encodeSecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
