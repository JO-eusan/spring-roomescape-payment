package roomescape.common.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.handler.PaymentExceptionHandler;
import roomescape.infrastructure.payment.toss.TossPaymentWithRestClient;

@TestConfiguration
public class TestPaymentConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEME = "Basic ";

    @Value("${security.toss.payment.secret-key}")
    private String secretKey;

    @Bean
    public TossPaymentWithRestClient tossPaymentWithRestClient(RestClient.Builder builder) {
        return new TossPaymentWithRestClient(builder
            .baseUrl("https://api.tosspayments.com/v1/payments")
            .defaultHeader(AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME + encodeSecretKey())
            .defaultStatusHandler(new PaymentExceptionHandler())
            .build());
    }

    private String encodeSecretKey() {
        return Base64.getEncoder().encodeToString("test-secret".getBytes());
    }
}
