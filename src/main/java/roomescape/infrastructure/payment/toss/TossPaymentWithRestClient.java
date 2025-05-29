package roomescape.infrastructure.payment.toss;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.PaymentServerException;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

public class TossPaymentWithRestClient implements TossPaymentWithHttpClient {

    private final RestClient restClient;

    public TossPaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TossPaymentConfirmResponseDto requestConfirmation(
        TossPaymentConfirmDto tossPaymentConfirmDto) {

        return restClient.post()
            .uri("/confirm")
            .body(tossPaymentConfirmDto)
            .retrieve()
            .body(TossPaymentConfirmResponseDto.class);
    }
}
