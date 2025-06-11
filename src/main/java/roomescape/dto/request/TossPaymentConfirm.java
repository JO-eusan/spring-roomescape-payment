package roomescape.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TossPaymentConfirm(
    @NotBlank String paymentKey,
    @NotBlank String orderId,
    @NotNull Long amount) {

}
