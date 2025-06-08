package roomescape.business.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.Member;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Theme;
import roomescape.business.model.Waiting;
import roomescape.business.vo.LoginMember;
import roomescape.business.vo.Period;
import roomescape.common.exception.DuplicatedException;
import roomescape.dto.request.ReservationSearch;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.persistence.MemberRepository;
import roomescape.persistence.ReservationTicketRepository;
import roomescape.persistence.ReservationTimeRepository;
import roomescape.persistence.ThemeRepository;
import roomescape.persistence.WaitingRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;

    public List<ReservationTicketResponse> getAllReservations() {
        return reservationTicketRepository.findAll().stream()
            .map(ReservationTicketResponse::from)
            .toList();
    }

    public List<ReservationTicketResponse> getReservationByFilter(ReservationSearch request) {
        return reservationTicketRepository.findByThemeIdAndMemberIdAndInPeriod(
                request.themeId(),
                request.memberId(),
                new Period(request.startDate(), request.endDate())).stream()
            .map(ReservationTicketResponse::from)
            .toList();
    }

    @Transactional
    public ReservationTicketResponse saveReservation(
        UserReservationRegister request, LoginMember loginMember) {

        ReservationTicket reservationTicket = createReservation(request, loginMember);
        validateDuplicatedReservation(reservationTicket);

        return ReservationTicketResponse.from(
            reservationTicketRepository.save(reservationTicket));
    }

    private ReservationTicket createReservation(
        UserReservationRegister request, LoginMember loginMember) {

        ReservationTime time = reservationTimeRepository.findById(request.timeId());
        Theme theme = themeRepository.findById(request.themeId());
        Member member = memberRepository.findById(loginMember.id());

        return new ReservationTicket(
            new Reservation(request.date(), time, theme, member, LocalDate.now()));
    }

    private void validateDuplicatedReservation(ReservationTicket reservationTicket) {
        if (reservationTicketRepository.isDuplicatedForDateAndReservationTime(
            reservationTicket.getDate(),
            reservationTicket.getReservationTime())) {
            throw new DuplicatedException("이미 예약이 존재합니다.");
        }
    }

    @Transactional
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
        ReservationTicket convertedReservationTicket = convertToReservation(nextWaiting);

        reservationTicketRepository.save(convertedReservationTicket);
        waitingRepository.delete(nextWaiting);
    }

    private ReservationTicket convertToReservation(Waiting nextWaiting) {
        return new ReservationTicket(
            new Reservation(
                nextWaiting.getReservationDate(),
                nextWaiting.getReservationTime(),
                nextWaiting.getTheme(),
                nextWaiting.getReservation().getMember(),
                nextWaiting.getRegisteredAt().toLocalDate()));
    }
}
