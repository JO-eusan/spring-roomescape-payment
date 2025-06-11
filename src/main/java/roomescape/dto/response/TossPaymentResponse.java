package roomescape.dto.response;

import roomescape.business.model.TossPayment;

public record TossPaymentResponse(
    String status,
    String paymentKey,
    String orderId,
    Long totalAmount) {

    public static TossPaymentResponse from(TossPayment tossPayment) {
        return new TossPaymentResponse(
            tossPayment.getStatus(),
            tossPayment.getPaymentKey(),
            tossPayment.getOrderId(),
            tossPayment.getTotalAmount()
        );
    }
}
