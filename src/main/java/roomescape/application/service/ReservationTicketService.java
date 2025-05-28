package roomescape.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationSearchDto;
import roomescape.dto.response.MemberReservationResponseDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.Waiting;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.WaitingRepository;
import roomescape.persistence.vo.Period;

@Service
@RequiredArgsConstructor
public class ReservationTicketService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final WaitingRepository waitingRepository;

    public List<ReservationTicketResponseDto> getAllReservations() {
        return reservationTicketRepository.findAll().stream()
            .map(ReservationTicketResponseDto::new)
            .toList();
    }

    public List<MemberReservationResponseDto> getReservationsOfMember(LoginMember loginMember) {
        List<ReservationTicket> reservationTickets = reservationTicketRepository.findForMember(
            loginMember.id());

        return reservationTickets.stream()
            .map(MemberReservationResponseDto::new)
            .toList();
    }

    public List<ReservationTicketResponseDto> searchReservations(
        ReservationSearchDto reservationSearchDto) {
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
