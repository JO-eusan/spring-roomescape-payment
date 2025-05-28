package roomescape.infrastructure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.PaymentServerException;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

public class TossPaymentWithRestClient implements TossPaymentWithHttpClient {

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
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                            InputStream inputStream = response.getBody();
                            TossPaymentErrorResponse tossPaymentErrorResponse = parseToTossPaymentErrorResponse(inputStream);
                            throw new PaymentServerException(tossPaymentErrorResponse.message());
                        }
                )
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            InputStream inputStream = response.getBody();
                            TossPaymentErrorResponse tossPaymentErrorResponse = parseToTossPaymentErrorResponse(inputStream);
                            throw new PaymentClientException(tossPaymentErrorResponse.message());
                        }
                )
                .body(TossPaymentConfirmResponseDto.class);
    }

    private TossPaymentErrorResponse parseToTossPaymentErrorResponse(InputStream bodyStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        return mapper.readValue(bodyStream, TossPaymentErrorResponse.class);
    }

    private record TossPaymentErrorResponse(
            String message,
            String code
    ) {
    }
}
