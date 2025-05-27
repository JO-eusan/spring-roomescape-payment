package roomescape.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.PaymentException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationSearchDto;
import roomescape.dto.request.ReservationTicketRegisterDto;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.MemberReservationResponseDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.model.TossPayment;
import roomescape.model.Waiting;
import roomescape.persistence.repository.MemberRepository;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;
import roomescape.persistence.repository.ThemeRepository;
import roomescape.persistence.repository.TossPaymentRepository;
import roomescape.persistence.repository.WaitingRepository;
import roomescape.persistence.vo.Period;
import roomescape.presentation.support.TossPaymentWithRestClient;

@Service
@RequiredArgsConstructor
public class ReservationTicketService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentWithRestClient tossPaymentWithRestClient;


    public ReservationTicketResponseDto saveReservation(ReservationTicketRegisterDto reservationTicketRegisterDto,
                                                        LoginMember loginMember) {
        ReservationTicket reservationTicket = createReservation(reservationTicketRegisterDto, loginMember);
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

        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = tossPaymentWithRestClient.requestConfirmation(
                tossPaymentConfirmDto);

        if (!tossPaymentConfirmResponseDto.status().equals("DONE")) {
            throw new PaymentException("승인되지 않은 결제 내역입니다.");
        }

        ReservationTicket savedReservationTicket = reservationTicketRepository.save(reservationTicket);
        tossPaymentRepository.save(tossPayment);

        return new ReservationTicketResponseDto(savedReservationTicket);
    }

    public List<ReservationTicketResponseDto> getAllReservations() {
        return reservationTicketRepository.findAll().stream()
                .map(ReservationTicketResponseDto::new)
                .toList();
    }

    public List<ReservationTicketResponseDto> searchReservations(ReservationSearchDto reservationSearchDto) {
        Long themeId = reservationSearchDto.themeId();
        Long memberId = reservationSearchDto.memberId();
        LocalDate startDate = reservationSearchDto.startDate();
        LocalDate endDate = reservationSearchDto.endDate();

        return reservationTicketRepository.findForThemeAndMemberInPeriod(
                        themeId,
                        memberId,
                        new Period(startDate, endDate)
                ).stream()
                .map(ReservationTicketResponseDto::new)
                .toList();
    }

    public void cancelReservation(Long id) {
        ReservationTicket reservationTicket = reservationTicketRepository.findById(id);
        reservationTicketRepository.deleteById(id);

        promoteNextWaitingToReservation(reservationTicket);
    }

    private void promoteNextWaitingToReservation(ReservationTicket reservationTicket) {
        Optional<Waiting> optionalNextWaiting = waitingRepository.findNextWaiting(
                reservationTicket.getDate(),
                reservationTicket.getReservationTime(),
                reservationTicket.getTheme()
        );

        if (optionalNextWaiting.isEmpty()) {
            return;
        }

        Waiting nextWaiting = optionalNextWaiting.get();

        promoteToReservation(nextWaiting);
        waitingRepository.delete(nextWaiting);
    }

    private void promoteToReservation(Waiting nextWaiting) {
        ReservationTicket convertedReservationTicket = convertToReservation(nextWaiting);
        reservationTicketRepository.save(convertedReservationTicket);
    }

    public List<MemberReservationResponseDto> getReservationsOfMember(LoginMember loginMember) {
        List<ReservationTicket> reservationTickets = reservationTicketRepository.findForMember(loginMember.id());

        return reservationTickets.stream()
                .map(MemberReservationResponseDto::new)
                .toList();
    }

    private ReservationTicket createReservation(ReservationTicketRegisterDto reservationTicketRegisterDto,
                                                LoginMember loginMember) {
        ReservationTime time = reservationTimeRepository.findById(reservationTicketRegisterDto.timeId());
        Theme theme = themeRepository.findById(reservationTicketRegisterDto.themeId());
        Member member = memberRepository.findById(loginMember.id());

        return reservationTicketRegisterDto.convertToReservation(time, theme, member);
    }

    private void assertReservationIsNotDuplicated(ReservationTicket reservationTicket) {
        if (reservationTicketRepository.isDuplicatedForDateAndReservationTime(reservationTicket.getDate(),
                reservationTicket.getReservationTime())) {
            throw new DuplicatedException("이미 예약이 존재합니다.");
        }
    }

    private ReservationTicket convertToReservation(Waiting nextWaiting) {
        return new ReservationTicket(
                new Reservation(
                        nextWaiting.getReservationDate(),
                        nextWaiting.getReservationTime(),
                        nextWaiting.getTheme(),
                        nextWaiting.getReservation().getMember(),
                        nextWaiting.getRegisteredAt().toLocalDate()
                ));
    }
}
