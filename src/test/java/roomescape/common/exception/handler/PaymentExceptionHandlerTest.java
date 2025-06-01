package roomescape.common.exception.handler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.common.config.TestPaymentConfig;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.PaymentServerException;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.infrastructure.payment.toss.TossPaymentWithRestClient;

@RestClientTest(value = TossPaymentWithRestClient.class)
@Import(TestPaymentConfig.class)
class PaymentExceptionHandlerTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    TossPaymentWithRestClient tossPaymentWithRestClient;

    @DisplayName("토스의 클라이언트 에러는 PaymentClientException을 발생시킨다")
    @Test
    void test1() {
        // given
        String errorCode = "UNAUTHORIZED_KEY";
        String errorResponseBody = """
                {
                  "code": "UNAUTHORIZED_KEY",
                  "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                }
            """.formatted(errorCode);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andExpect(method(HttpMethod.POST)).andExpect(content().json("""
                {
                       "paymentKey": "sample-key",
                       "orderId": "order-123",
                       "amount": 10000
                }
                """)).andRespond(
                withStatus(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponseBody));

        // when & then
        TossPaymentConfirmDto requestDto = new TossPaymentConfirmDto("sample-key", "order-123",
            10000L);
        assertThatThrownBy(
            () -> tossPaymentWithRestClient.requestConfirmation(requestDto)).isInstanceOf(
            PaymentClientException.class);

    }

    @DisplayName("토스의 서버 에러는 PaymentServerException을 발생시킨다")
    @Test
    void test2() {
        // given
        String errorCode = "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING";
        String errorResponseBody = """
                {
                  "code": "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
                  "message": "결제가 완료되지 않았어요. 다시 시도해주세요."
                }
            """.formatted(errorCode);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andExpect(method(HttpMethod.POST)).andExpect(content().json("""
                {
                       "paymentKey": "sample-key",
                       "orderId": "order-123",
                       "amount": 10000
                   }
                """)).andRespond(
                withStatus(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponseBody));

        // when & then
        TossPaymentConfirmDto requestDto = new TossPaymentConfirmDto("sample-key", "order-123",
            10000L);
        assertThatThrownBy(
            () -> tossPaymentWithRestClient.requestConfirmation(requestDto)).isInstanceOf(
            PaymentServerException.class);

    }
}
