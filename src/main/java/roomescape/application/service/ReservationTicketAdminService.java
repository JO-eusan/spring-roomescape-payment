package roomescape.application.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.dto.request.AdminReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.persistence.repository.MemberRepository;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;
import roomescape.persistence.repository.ThemeRepository;

@Service
@RequiredArgsConstructor
public class ReservationTicketAdminService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final MemberRepository memberRepository;

    public ReservationTicketResponse saveReservation(AdminReservationRegister registerDto) {
        Member member = memberRepository.findById(registerDto.memberId());
        ReservationTime reservationTime = reservationTimeRepository.findById(registerDto.timeId());
        Theme theme = themeRepository.findById(registerDto.themeId());

        ReservationTicket reservationTicket = new ReservationTicket(
                new Reservation(registerDto.date(), reservationTime, theme, member, LocalDate.now()));

        return ReservationTicketResponse.from(reservationTicketRepository.save(reservationTicket));
    }
}
