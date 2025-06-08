package roomescape.persistence;

import java.time.LocalTime;
import java.util.List;
import roomescape.business.model.ReservationTime;

public interface ReservationTimeRepository {

    List<ReservationTime> findAll();

    ReservationTime findById(Long id);

    boolean isDuplicatedStartAt(LocalTime startAt);

    ReservationTime save(ReservationTime reservationTime);

    void deleteById(Long id);
}
