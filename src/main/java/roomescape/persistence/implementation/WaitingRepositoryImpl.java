package roomescape.persistence.implementation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Theme;
import roomescape.business.model.Waiting;
import roomescape.common.exception.NotFoundException;
import roomescape.infrastructure.db.WaitingJpaRepository;
import roomescape.persistence.WaitingRepository;

@Repository
@RequiredArgsConstructor
public class WaitingRepositoryImpl implements WaitingRepository {

    private final WaitingJpaRepository waitingJpaRepository;

    @Override
    public List<Waiting> findAll() {
        return waitingJpaRepository.findAll();
    }

    @Override
    public List<Waiting> findByMemberId(Long memberId) {
        return waitingJpaRepository.findByReservation_MemberId(memberId);
    }

    @Override
    public Waiting findById(Long id) {
        return waitingJpaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 웨이팅입니다."));
    }

    @Override
    public Optional<Waiting> findNextWaiting(
        LocalDate date, ReservationTime reservationTime, Theme theme) {
        return waitingJpaRepository.findEarliestWaitingBy(date, reservationTime, theme);
    }

    @Override
    public int countWaitingBefore(Waiting waiting) {
        return waitingJpaRepository.countWaitingBefore(
            waiting.getRegisteredAt(),
            waiting.getReservationDate(),
            waiting.getTheme(),
            waiting.getReservationTime()
        );
    }

    @Override
    public Waiting save(Waiting waiting) {
        return waitingJpaRepository.save(waiting);
    }

    @Override
    public void delete(Waiting waiting) {
        waitingJpaRepository.delete(waiting);
    }

    @Override
    public void deleteById(Long id) {
        waitingJpaRepository.deleteById(id);
    }
}
