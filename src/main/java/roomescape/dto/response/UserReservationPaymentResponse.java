package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.business.model.TossPayment;

public record UserReservationPaymentResponse(
    Long id,
    String theme,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
    @JsonFormat(pattern = "HH:mm") LocalTime time,
    String paymentKey,
    Long totalAmount) {

    public static UserReservationPaymentResponse from(TossPayment tossPayment) {
        return new UserReservationPaymentResponse(
            tossPayment.getReservationTicket().getId(),
            tossPayment.getReservationTicket().getTheme().getName(),
            tossPayment.getReservationTicket().getDate(),
            tossPayment.getReservationTicket().getReservationTime().getStartAt(),
            tossPayment.getPaymentKey(),
            tossPayment.getTotalAmount()
        );
    }
}
