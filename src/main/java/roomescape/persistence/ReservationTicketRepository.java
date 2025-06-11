package roomescape.persistence;

import java.time.LocalDate;
import java.util.List;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.business.vo.Period;

public interface ReservationTicketRepository {

    List<ReservationTicket> findAll();

    List<ReservationTicket> findByThemeIdAndDate(Long themeId, LocalDate date);

    List<ReservationTicket> findByThemeIdAndMemberIdAndInPeriod(
        Long themeId,
        Long memberId,
        Period period
    );

    ReservationTicket findById(Long id);

    boolean isExistForThemeAndReservationTimeOnDate(Reservation reservation);

    boolean isDuplicatedForDateAndReservationTime(LocalDate date, ReservationTime time);

    ReservationTicket save(ReservationTicket reservationTicket);

    void deleteById(Long id);
}
