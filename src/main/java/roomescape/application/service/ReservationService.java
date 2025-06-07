package roomescape.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.DuplicatedException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationSearch;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.response.UserReservationResponse;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.model.Waiting;
import roomescape.persistence.repository.MemberRepository;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;
import roomescape.persistence.repository.ThemeRepository;
import roomescape.persistence.repository.WaitingRepository;
import roomescape.persistence.vo.Period;

@Service
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

    public List<UserReservationResponse> getReservationsOfMember(LoginMember loginMember) {
        List<ReservationTicket> reservationTickets = reservationTicketRepository.findForMember(
            loginMember.id());

        return reservationTickets.stream()
            .map(UserReservationResponse::from)
            .toList();
    }

    public List<ReservationTicketResponse> searchReservations(
        ReservationSearch reservationSearch) {
        Long themeId = reservationSearch.themeId();
        Long memberId = reservationSearch.memberId();
        LocalDate startDate = reservationSearch.startDate();
        LocalDate endDate = reservationSearch.endDate();

        return reservationTicketRepository.findForThemeAndMemberInPeriod(
                themeId,
                memberId,
                new Period(startDate, endDate)
            ).stream()
            .map(ReservationTicketResponse::from)
            .toList();
    }

    public ReservationTicketResponse saveReservation(
        UserReservationRegister request, LoginMember loginMember) {

        ReservationTicket reservationTicket = createReservation(request, loginMember);
        assertReservationIsNotDuplicated(reservationTicket);

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

    private void assertReservationIsNotDuplicated(ReservationTicket reservationTicket) {
        if (reservationTicketRepository.isDuplicatedForDateAndReservationTime(
            reservationTicket.getDate(),
            reservationTicket.getReservationTime())) {
            throw new DuplicatedException("이미 예약이 존재합니다.");
        }
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
