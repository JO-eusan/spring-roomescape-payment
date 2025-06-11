package roomescape.business.service.coordinator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.service.ReservationService;
import roomescape.business.service.TossPaymentService;
import roomescape.business.vo.LoginMember;
import roomescape.common.exception.PaymentClientException;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.dto.response.TossPaymentResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationPaymentCoordinator {

    private final ReservationService reservationService;
    private final TossPaymentService tossPaymentService;

    public ReservationTicketResponse saveReservationWithPayment(
        UserReservationRegister userReservationRegister, LoginMember loginMember) {

        ReservationTicketResponse reservationTicketResponse = reservationService.saveReservation(
            userReservationRegister, loginMember);
        TossPaymentResponse tossPaymentResponse = tossPaymentService.savePayment(
            userReservationRegister, reservationTicketResponse.id());

        if (!tossPaymentResponse.status().equals("DONE")) {
            throw new PaymentClientException("승인되지 않은 결제 내역입니다.");
        }

        return reservationTicketResponse;
    }
}
