package roomescape.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Theme;
import roomescape.business.model.Waiting;

public interface WaitingRepository {

    List<Waiting> findAll();

    List<Waiting> findByMemberId(Long memberId);

    Waiting findById(Long id);

    Optional<Waiting> findNextWaiting(LocalDate date, ReservationTime reservationTime, Theme theme);

    int countWaitingBefore(Waiting waiting);

    Waiting save(Waiting waiting);

    void delete(Waiting waiting);

    void deleteById(Long id);
}
