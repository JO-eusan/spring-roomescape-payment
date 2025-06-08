package roomescape.infrastructure.db;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Theme;

public interface ReservationTicketJpaRepository extends JpaRepository<ReservationTicket, Long> {

    List<ReservationTicket> findByReservation_ThemeIdAndReservation_MemberIdAndReservation_DateBetween(
        Long themeId, Long memberId, LocalDate dateAfter, LocalDate dateBefore);

    List<ReservationTicket> findByReservation_ThemeIdAndReservation_Date(
        Long themeId, LocalDate date);

    List<ReservationTicket> findByReservation_MemberId(Long memberId);

    List<ReservationTicket> findByReservation_ThemeId(Long themeId);

    List<ReservationTicket> findByReservation_ReservationTimeId(Long timeId);

    Optional<ReservationTicket> findByReservation_DateAndReservation_ReservationTime(
        LocalDate date, ReservationTime time);

    Optional<ReservationTicket> findByReservation_ThemeAndReservation_ReservationTimeAndReservation_Date(
        Theme theme, ReservationTime reservationTime, LocalDate date);
}
