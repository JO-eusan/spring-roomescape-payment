package roomescape.common.exception.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.PaymentServerException;

public class PaymentErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response)
        throws IOException {
        InputStream inputStream = response.getBody();
        String message = parseToErrorResponse(inputStream).message();

        if (response.getStatusCode().is4xxClientError()) {
            throw new PaymentClientException(message);
        } else if (response.getStatusCode().is5xxServerError()) {
            throw new PaymentServerException(message);
        }
    }

    private TossPaymentErrorResponse parseToErrorResponse(InputStream bodyStream)
        throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(bodyStream, TossPaymentErrorResponse.class);
    }

    private record TossPaymentErrorResponse(
        String message,
        String code) {

    }
}
