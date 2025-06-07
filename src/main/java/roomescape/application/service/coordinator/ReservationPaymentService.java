package roomescape.application.service.coordinator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.service.ReservationTicketService;
import roomescape.application.service.TossPaymentService;
import roomescape.common.exception.PaymentClientException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationTicketRegisterDto;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.dto.response.TossPaymentResponse;

@Service
@RequiredArgsConstructor
public class ReservationPaymentService {

    private final ReservationTicketService reservationTicketService;
    private final TossPaymentService tossPaymentService;

    public ReservationTicketResponse saveReservationWithPayment(
        ReservationTicketRegisterDto reservationTicketRegisterDto,
        LoginMember loginMember) {

        ReservationTicketResponse reservationTicketResponse = reservationTicketService.saveReservation(
            reservationTicketRegisterDto, loginMember);
        TossPaymentResponse tossPaymentResponse = tossPaymentService.savePayment(
            reservationTicketRegisterDto, reservationTicketResponse.id());

        if (!tossPaymentResponse.status().equals("DONE")) {
            throw new PaymentClientException("승인되지 않은 결제 내역입니다.");
        }

        return reservationTicketResponse;
    }
}
