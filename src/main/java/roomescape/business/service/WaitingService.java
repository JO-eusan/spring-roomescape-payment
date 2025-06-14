package roomescape.business.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.Member;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Theme;
import roomescape.business.model.Waiting;
import roomescape.business.vo.LoginMember;
import roomescape.common.exception.OperationNotAllowedException;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dto.request.WaitingRegister;
import roomescape.dto.response.UserWaitingResponse;
import roomescape.dto.response.WaitingResponse;
import roomescape.persistence.MemberRepository;
import roomescape.persistence.ReservationTicketRepository;
import roomescape.persistence.ReservationTimeRepository;
import roomescape.persistence.ThemeRepository;
import roomescape.persistence.WaitingRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTicketRepository reservationTicketRepository;

    public List<UserWaitingResponse> getUserWaitings(LoginMember loginMember) {
        return waitingRepository.findByMemberId(loginMember.id()).stream()
            .map(waiting -> UserWaitingResponse.from(
                waiting, waitingRepository.countWaitingBefore(waiting) + 1L)).toList();
    }

    @Transactional
    public WaitingResponse saveWaiting(WaitingRegister request, LoginMember loginMember) {
        Reservation reservation = createReservation(request, loginMember);
        validateReservationExistsForWaiting(reservation);

        Waiting savedWaiting = waitingRepository.save(
            new Waiting(LocalDateTime.now(), reservation));

        return WaitingResponse.from(savedWaiting);
    }

    private Reservation createReservation(WaitingRegister request, LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.id());
        ReservationTime reservationTime = reservationTimeRepository.findById(request.timeId());
        Theme theme = themeRepository.findById(request.themeId());

        return new Reservation(request.date(), reservationTime, theme, member, LocalDate.now());
    }

    private void validateReservationExistsForWaiting(Reservation reservation) {
        if (!reservationTicketRepository.isExistForThemeAndReservationTimeOnDate(reservation)) {
            throw new OperationNotAllowedException("예약 내역이 존재하지 않아 대기를 등록할 수 없습니다.");
        }
    }

    @Transactional
    public void deleteWaiting(Long id, LoginMember loginMember) {
        Waiting waiting = waitingRepository.findById(id);
        Member member = memberRepository.findById(loginMember.id());

        if (!waiting.ownBy(member)) {
            throw new UnauthorizedException("해당 객체를 삭제할 수 있는 권한이 없습니다.");
        }

        waitingRepository.delete(waiting);
    }
}
