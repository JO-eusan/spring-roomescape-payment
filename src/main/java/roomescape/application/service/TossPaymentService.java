package roomescape.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.dto.request.ReservationTicketRegisterDto;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentResponse;
import roomescape.model.TossPayment;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.TossPaymentRepository;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentWithHttpClient tossPaymentWithHttpClient;

    public TossPaymentResponse savePayment(
        ReservationTicketRegisterDto reservationTicketRegisterDto,
        Long reservationTicketId) {

        TossPaymentResponse tossPaymentResponse = tossPaymentWithHttpClient
            .requestConfirmation(new TossPaymentConfirmDto(
                reservationTicketRegisterDto.paymentKey(),
                reservationTicketRegisterDto.orderId(),
                reservationTicketRegisterDto.amount()
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
