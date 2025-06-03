package roomescape.dto.response;

import roomescape.model.TossPayment;

public record TossPaymentConfirmResponseDto(
        String status,
        String paymentKey,
        String orderId
) {

    public TossPaymentConfirmResponseDto(TossPayment tossPayment) {
        this(
            tossPayment.getStatus(),
            tossPayment.getPaymentKey(),
            tossPayment.getOrderId()
        );
    }
}
