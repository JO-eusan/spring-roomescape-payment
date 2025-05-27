package roomescape.infrastructure;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import roomescape.application.support.TossPaymentService;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

public class TossPaymentWithRestClient implements TossPaymentService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEME = "Basic ";

    @Value("${security.toss.payment.secret-key}")
    private String secretKey;

    private final RestClient restClient;

    public TossPaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TossPaymentConfirmResponseDto requestConfirmation(TossPaymentConfirmDto tossPaymentConfirmDto) {
        String encodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());

        return restClient.post()
                .uri("/confirm")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME + encodedSecretKey)
                .body(tossPaymentConfirmDto)
                .retrieve()
                .body(TossPaymentConfirmResponseDto.class);
    }
}
