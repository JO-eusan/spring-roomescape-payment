package roomescape.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.PaymentClientException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationTicketRegisterDto;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;
import roomescape.model.Member;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.model.TossPayment;
import roomescape.persistence.repository.MemberRepository;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;
import roomescape.persistence.repository.ThemeRepository;
import roomescape.persistence.repository.TossPaymentRepository;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentWithHttpClient tossPaymentWithHttpClient;
    private final ReservationTicketRepository reservationTicketRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReservationTicketResponseDto saveReservation(
        ReservationTicketRegisterDto reservationTicketRegisterDto,
        LoginMember loginMember) {
        ReservationTicket reservationTicket = createReservation(reservationTicketRegisterDto,
            loginMember);
        assertReservationIsNotDuplicated(reservationTicket);

        TossPayment tossPayment = new TossPayment(
            reservationTicketRegisterDto.paymentKey(),
            reservationTicketRegisterDto.orderId(),
            reservationTicketRegisterDto.amount(),
            reservationTicket
        );

        TossPaymentConfirmDto tossPaymentConfirmDto = new TossPaymentConfirmDto(
            reservationTicketRegisterDto.paymentKey(),
            reservationTicketRegisterDto.orderId(),
            reservationTicketRegisterDto.amount()
        );

        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = tossPaymentWithHttpClient.requestConfirmation(
            tossPaymentConfirmDto);

        if (!tossPaymentConfirmResponseDto.status().equals("DONE")) {
            throw new PaymentClientException("승인되지 않은 결제 내역입니다.");
        }

        ReservationTicket savedReservationTicket = reservationTicketRepository.save(
            reservationTicket);
        tossPaymentRepository.save(tossPayment);

        return new ReservationTicketResponseDto(savedReservationTicket);
    }

    private ReservationTicket createReservation(
        ReservationTicketRegisterDto reservationTicketRegisterDto,
        LoginMember loginMember) {
        ReservationTime time = reservationTimeRepository.findById(
            reservationTicketRegisterDto.timeId());
        Theme theme = themeRepository.findById(reservationTicketRegisterDto.themeId());
        Member member = memberRepository.findById(loginMember.id());

        return reservationTicketRegisterDto.convertToReservation(time, theme, member);
    }

    private void assertReservationIsNotDuplicated(ReservationTicket reservationTicket) {
        if (reservationTicketRepository.isDuplicatedForDateAndReservationTime(
            reservationTicket.getDate(),
            reservationTicket.getReservationTime())) {
            throw new DuplicatedException("이미 예약이 존재합니다.");
        }
    }
}
