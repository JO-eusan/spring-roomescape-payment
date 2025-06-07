package roomescape.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.dto.request.TossPaymentConfirm;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.response.TossPaymentResponse;
import roomescape.infrastructure.payment.TossPaymentRestClient;
import roomescape.business.model.TossPayment;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.TossPaymentRepository;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentRestClient tossPaymentRestClient;

    public TossPaymentResponse savePayment(
        UserReservationRegister userReservationRegister,
        Long reservationTicketId) {

        TossPaymentResponse tossPaymentResponse = tossPaymentRestClient
            .requestConfirmation(new TossPaymentConfirm(
                userReservationRegister.paymentKey(),
                userReservationRegister.orderId(),
                userReservationRegister.amount()
            ));

        return TossPaymentResponse.from(
            tossPaymentRepository.save(
                createPayment(tossPaymentResponse, reservationTicketId)));
    }

    private TossPayment createPayment(
        TossPaymentResponse tossPaymentResponse,
        Long reservationTicketId) {

        return new TossPayment(
            tossPaymentResponse.paymentKey(),
            tossPaymentResponse.orderId(),
            tossPaymentResponse.status(),
            reservationTicketRepository.findById(reservationTicketId)
        );
    }
}
