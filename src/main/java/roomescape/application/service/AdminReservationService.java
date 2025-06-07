package roomescape.application.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.dto.request.AdminReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.dto.response.WaitingResponse;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.persistence.repository.MemberRepository;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;
import roomescape.persistence.repository.ThemeRepository;
import roomescape.persistence.repository.WaitingRepository;

@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;

    public ReservationTicketResponse saveReservation(AdminReservationRegister request) {
        return ReservationTicketResponse.from(
            reservationTicketRepository.save(createReservation(request)));
    }

    private ReservationTicket createReservation(AdminReservationRegister request) {
        Member member = memberRepository.findById(request.memberId());
        ReservationTime time = reservationTimeRepository.findById(request.timeId());
        Theme theme = themeRepository.findById(request.themeId());

        return new ReservationTicket(
            new Reservation(request.date(), time, theme, member, LocalDate.now()));
    }

    public List<WaitingResponse> getAllWaitings() {
        return waitingRepository.findAll().stream()
            .map(WaitingResponse::from)
            .toList();
    }

    public void rejectWaiting(Long id) {
        waitingRepository.rejectById(id);
    }
}
