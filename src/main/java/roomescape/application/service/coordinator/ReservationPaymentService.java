package roomescape.application.service.coordinator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.service.ReservationTicketService;
import roomescape.application.service.TossPaymentService;
import roomescape.common.exception.PaymentClientException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationTicketRegisterDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

@Service
@RequiredArgsConstructor
public class ReservationPaymentService {

    private final ReservationTicketService reservationTicketService;
    private final TossPaymentService tossPaymentService;

    public ReservationTicketResponseDto saveReservationWithPayment(
        ReservationTicketRegisterDto reservationTicketRegisterDto,
        LoginMember loginMember) {

        ReservationTicketResponseDto reservationTicketResponseDto = reservationTicketService.saveReservation(
            reservationTicketRegisterDto, loginMember);
        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = tossPaymentService.savePayment(
            reservationTicketRegisterDto, reservationTicketResponseDto.id());

        if (!tossPaymentConfirmResponseDto.status().equals("DONE")) {
            throw new PaymentClientException("승인되지 않은 결제 내역입니다.");
        }

        return reservationTicketResponseDto;
    }
}
