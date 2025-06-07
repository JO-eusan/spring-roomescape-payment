package roomescape.dto.response;

import roomescape.model.TossPayment;

public record TossPaymentResponse(
    String status,
    String paymentKey,
    String orderId) {

    public static TossPaymentResponse from(TossPayment tossPayment) {
        return new TossPaymentResponse(
            tossPayment.getStatus(),
            tossPayment.getPaymentKey(),
            tossPayment.getOrderId()
        );
    }
}
