package roomescape.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.OperationNotAllowedException;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.WaitingRegister;
import roomescape.dto.response.UserWaitingResponse;
import roomescape.dto.response.WaitingResponse;
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

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTicketRepository reservationTicketRepository;

    @Transactional
    public WaitingResponse registerWaiting(LoginMember loginMember,
        WaitingRegister waitingRegister) {
        Member member = memberRepository.findById(loginMember.id());
        ReservationTime reservationTime = reservationTimeRepository.findById(
            waitingRegister.timeId());
        Theme theme = themeRepository.findById(waitingRegister.themeId());

        Reservation reservation = new Reservation(
            waitingRegister.date(),
            reservationTime,
            theme,
            member,
            LocalDate.now()
        );

        validateReservationExistsForWaiting(reservation);

        Waiting waiting = new Waiting(LocalDateTime.now(), reservation);

        Waiting savedWaiting = waitingRepository.save(waiting);

        return WaitingResponse.from(savedWaiting);
    }

    private void validateReservationExistsForWaiting(Reservation reservation) {
        Optional<ReservationTicket> foundReservationTicket = reservationTicketRepository.findForThemeAndReservationTimeOnDate(
            reservation);

        if (foundReservationTicket.isEmpty()) {
            throw new OperationNotAllowedException("예약 내역이 존재하지 않아 대기를 등록할 수 없습니다.");
        }
    }

    @Transactional
    public void deleteWaiting(LoginMember loginMember, Long id) {
        Waiting waiting = waitingRepository.findById(id);
        Member member = memberRepository.findById(loginMember.id());

        if (!waiting.ownBy(member)) {
            throw new UnauthorizedException("해당 객체를 삭제할 수 있는 권한이 없습니다.");
        }

        waitingRepository.delete(waiting);
    }

    public List<UserWaitingResponse> getMyWaitings(LoginMember loginMember) {
        List<Waiting> myWaitings = waitingRepository.findForMember(loginMember.id());

        return myWaitings.stream()
            .map(waiting -> UserWaitingResponse.from(waiting,
                waitingRepository.countWaitingBefore(waiting) + 1L))
            .toList();
    }
}

