package roomescape.business.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.Member;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Theme;
import roomescape.dto.request.AdminReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.dto.response.WaitingResponse;
import roomescape.persistence.MemberRepository;
import roomescape.persistence.ReservationTicketRepository;
import roomescape.persistence.ReservationTimeRepository;
import roomescape.persistence.ThemeRepository;
import roomescape.persistence.WaitingRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;

    public List<WaitingResponse> getAllWaitings() {
        return waitingRepository.findAll().stream()
            .map(WaitingResponse::from)
            .toList();
    }

    @Transactional
    public ReservationTicketResponse saveReservation(AdminReservationRegister request) {
        return ReservationTicketResponse.from(
            reservationTicketRepository.save(createReservationTicket(request)));
    }

    private ReservationTicket createReservationTicket(AdminReservationRegister request) {
        Member member = memberRepository.findById(request.memberId());
        ReservationTime time = reservationTimeRepository.findById(request.timeId());
        Theme theme = themeRepository.findById(request.themeId());

        return new ReservationTicket(
            new Reservation(request.date(), time, theme, member, LocalDate.now()));
    }

    @Transactional
    public void rejectWaitingById(Long id) {
        waitingRepository.deleteById(id);
    }
}
