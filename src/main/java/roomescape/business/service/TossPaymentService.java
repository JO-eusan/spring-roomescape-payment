package roomescape.business.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.TossPayment;
import roomescape.business.vo.LoginMember;
import roomescape.dto.request.TossPaymentConfirm;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.response.TossPaymentResponse;
import roomescape.dto.response.UserReservationPaymentResponse;
import roomescape.infrastructure.payment.TossPaymentRestClient;
import roomescape.persistence.ReservationTicketRepository;
import roomescape.persistence.TossPaymentRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TossPaymentService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentRestClient tossPaymentRestClient;

    public List<UserReservationPaymentResponse> getReservationWithPaymentOfMember(
        LoginMember loginMember) {
        return tossPaymentRepository.findByMemberId(loginMember.id()).stream()
            .map(UserReservationPaymentResponse::from)
            .toList();
    }

    @Transactional
    public TossPaymentResponse savePayment(
        UserReservationRegister userReservationRegister, Long reservationTicketId) {

        TossPaymentResponse tossPaymentResponse = tossPaymentRestClient.requestConfirmation(
            new TossPaymentConfirm(
                userReservationRegister.paymentKey(),
                userReservationRegister.orderId(),
                userReservationRegister.amount()));

        return TossPaymentResponse.from(
            tossPaymentRepository.save(createPayment(tossPaymentResponse, reservationTicketId)));
    }

    private TossPayment createPayment(
        TossPaymentResponse tossPaymentResponse, Long reservationTicketId) {

        return new TossPayment(
            tossPaymentResponse.paymentKey(),
            tossPaymentResponse.orderId(),
            tossPaymentResponse.status(),
            tossPaymentResponse.totalAmount(),
            reservationTicketRepository.findById(reservationTicketId)
        );
    }
}
