package roomescape.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.dto.request.ReservationTicketRegisterDto;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;
import roomescape.model.TossPayment;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.TossPaymentRepository;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentWithHttpClient tossPaymentWithHttpClient;

    public TossPaymentConfirmResponseDto savePayment(
        ReservationTicketRegisterDto reservationTicketRegisterDto,
        Long reservationTicketId) {

        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = tossPaymentWithHttpClient
            .requestConfirmation(new TossPaymentConfirmDto(
                reservationTicketRegisterDto.paymentKey(),
                reservationTicketRegisterDto.orderId(),
                reservationTicketRegisterDto.amount()
            ));

        return new TossPaymentConfirmResponseDto(
            tossPaymentRepository.save(
                createPayment(tossPaymentConfirmResponseDto, reservationTicketId)));
    }

    private TossPayment createPayment(
        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto,
        Long reservationTicketId) {

        return new TossPayment(
            tossPaymentConfirmResponseDto.paymentKey(),
            tossPaymentConfirmResponseDto.orderId(),
            tossPaymentConfirmResponseDto.status(),
            reservationTicketRepository.findById(reservationTicketId)
        );
    }
}
