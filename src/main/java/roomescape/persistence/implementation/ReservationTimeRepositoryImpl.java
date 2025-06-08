package roomescape.persistence.implementation;

import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.business.model.ReservationTime;
import roomescape.common.exception.NotFoundException;
import roomescape.infrastructure.db.ReservationTimeJpaRepository;
import roomescape.persistence.ReservationTimeRepository;

@Repository
@RequiredArgsConstructor
public class ReservationTimeRepositoryImpl implements ReservationTimeRepository {

    private final ReservationTimeJpaRepository reservationTimeJpaRepository;

    @Override
    public List<ReservationTime> findAll() {
        return reservationTimeJpaRepository.findAll();
    }

    @Override
    public ReservationTime findById(Long id) {
        return reservationTimeJpaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 예약 시각입니다."));
    }

    @Override
    public boolean isDuplicatedStartAt(LocalTime startAt) {
        return reservationTimeJpaRepository.existsByStartAt(startAt);
    }

    @Override
    public ReservationTime save(ReservationTime reservationTime) {
        return reservationTimeJpaRepository.save(reservationTime);
    }

    @Override
    public void deleteById(Long id) {
        reservationTimeJpaRepository.deleteById(id);
    }
}
