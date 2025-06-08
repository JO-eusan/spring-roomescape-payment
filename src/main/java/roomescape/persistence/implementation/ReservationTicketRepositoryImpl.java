package roomescape.persistence.implementation;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.common.exception.NotFoundException;
import roomescape.infrastructure.db.ReservationTicketJpaRepository;
import roomescape.persistence.ReservationTicketRepository;
import roomescape.business.vo.Period;

@Repository
@RequiredArgsConstructor
public class ReservationTicketRepositoryImpl implements ReservationTicketRepository {

    private final ReservationTicketJpaRepository reservationTicketJpaRepository;

    @Override
    public List<ReservationTicket> findAll() {
        return reservationTicketJpaRepository.findAll();
    }

    @Override
    public List<ReservationTicket> findByMemberId(Long memberId) {
        return reservationTicketJpaRepository.findByReservation_MemberId(memberId);
    }

    @Override
    public List<ReservationTicket> findByThemeIdAndDate(Long themeId, LocalDate date) {
        return reservationTicketJpaRepository.findByReservation_ThemeIdAndReservation_Date(
            themeId, date);
    }

    @Override
    public List<ReservationTicket> findByThemeIdAndMemberIdAndInPeriod(
        Long themeId, Long memberId, Period period) {
        return reservationTicketJpaRepository.findByReservation_ThemeIdAndReservation_MemberIdAndReservation_DateBetween(
            themeId, memberId, period.startDate(), period.endDate());
    }

    @Override
    public ReservationTicket findById(Long id) {
        return reservationTicketJpaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("해당 예약 내역을 찾을 수 없습니다."));
    }

    @Override
    public boolean isExistForThemeAndReservationTimeOnDate(Reservation reservation) {
        return reservationTicketJpaRepository.findByReservation_ThemeAndReservation_ReservationTimeAndReservation_Date(
                reservation.getTheme(), reservation.getReservationTime(), reservation.getDate())
            .isPresent();
    }

    @Override
    public boolean isDuplicatedForDateAndReservationTime(LocalDate date, ReservationTime time) {
        return reservationTicketJpaRepository.findByReservation_DateAndReservation_ReservationTime(
            date, time).isPresent();
    }

    @Override
    public ReservationTicket save(ReservationTicket reservationTicket) {
        return reservationTicketJpaRepository.save(reservationTicket);
    }

    @Override
    public void deleteById(Long id) {
        reservationTicketJpaRepository.deleteById(id);
    }
}
