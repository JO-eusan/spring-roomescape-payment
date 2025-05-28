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

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEME = "Basic ";

    @Value("${security.toss.payment.secret-key}")
    private String secretKey;

    private final RestClient restClient;

    public TossPaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TossPaymentConfirmResponseDto requestConfirmation(
        TossPaymentConfirmDto tossPaymentConfirmDto) {
        String encodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());

        return restClient.post()
            .uri("/confirm")
            .header(AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME + encodedSecretKey)
            .body(tossPaymentConfirmDto)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                    InputStream inputStream = response.getBody();
                    TossPaymentErrorResponse tossPaymentErrorResponse = parseToTossPaymentErrorResponse(
                        inputStream);
                    checkClientError(tossPaymentErrorResponse);
                    checkServerError(tossPaymentErrorResponse);
                }
            )
            .body(TossPaymentConfirmResponseDto.class);
    }

    private static void checkServerError(TossPaymentErrorResponse tossPaymentErrorResponse) {
        Optional<TossPaymentErrorCodeForServer> tossPaymentErrorCodeForServer = TossPaymentErrorCodeForServer.of(
            tossPaymentErrorResponse.code);
        if (tossPaymentErrorCodeForServer.isPresent()) {
            throw new PaymentServerException(
                tossPaymentErrorCodeForServer.get().getMessage());
        }
    }

    private static void checkClientError(TossPaymentErrorResponse tossPaymentErrorResponse) {
        Optional<TossPaymentErrorCodeForClient> tossPaymentErrorCodeForClient = TossPaymentErrorCodeForClient.of(
            tossPaymentErrorResponse.code);
        if (tossPaymentErrorCodeForClient.isPresent()) {
            throw new PaymentClientException(
                tossPaymentErrorCodeForClient.get().getMessage());
        }
    }

    private TossPaymentErrorResponse parseToTossPaymentErrorResponse(InputStream bodyStream)
        throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false);
        return mapper.readValue(bodyStream, TossPaymentErrorResponse.class);
    }

    private record TossPaymentErrorResponse(
        String message,
        String code
    ) {

    }
}
