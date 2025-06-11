package roomescape.infrastructure.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import roomescape.dto.request.TossPaymentConfirm;
import roomescape.dto.response.TossPaymentResponse;

@RequiredArgsConstructor
public class TossPaymentRestClient {

    private final RestClient restClient;

    public TossPaymentResponse requestConfirmation(TossPaymentConfirm tossPaymentConfirm) {
        return restClient.post()
            .uri("/confirm")
            .body(tossPaymentConfirm)
            .retrieve()
            .body(TossPaymentResponse.class);
    }
}
