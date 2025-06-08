package roomescape.business.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.common.exception.DuplicatedException;
import roomescape.dto.request.ReservationTimeRegister;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.persistence.ReservationTicketRepository;
import roomescape.persistence.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationTicketRepository reservationTicketRepository;

    public List<ReservationTimeResponse> getAllTimes() {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();

        return reservationTimes.stream()
            .map(ReservationTimeResponse::from)
            .toList();
    }

    public List<ReservationTimeResponse> getAvailableTimes(String date, Long themeId) {
        List<ReservationTicket> reservationTickets = getReservationsBy(date, themeId);
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        Set<ReservationTime> nonDuplicatedReservationTimes = getReservationTimes(
            reservationTickets);

        reservationTimes.removeAll(nonDuplicatedReservationTimes);

        return getAvailableReservationTimes(reservationTimes, nonDuplicatedReservationTimes);
    }

    private List<ReservationTicket> getReservationsBy(String date, Long themeId) {
        LocalDate parsedDate = LocalDate.parse(date);
        return reservationTicketRepository.findByThemeIdAndDate(themeId, parsedDate);
    }

    private Set<ReservationTime> getReservationTimes(List<ReservationTicket> reservationTickets) {
        return reservationTickets.stream()
            .map(ReservationTicket::getReservationTime)
            .collect(Collectors.toSet());
    }

    private List<ReservationTimeResponse> getAvailableReservationTimes(
        List<ReservationTime> reservationTimes,
        Set<ReservationTime> nonDuplicatedReservationTimes) {
        List<ReservationTimeResponse> availableReservationTimes = getAvailableReservationTimes(
            reservationTimes);
        List<ReservationTimeResponse> nonAvailableReservationTimes = getAvailableReservationTimes(
            nonDuplicatedReservationTimes);

        return Stream.of(availableReservationTimes, nonAvailableReservationTimes)
            .flatMap(Collection::stream)
            .toList();
    }

    private List<ReservationTimeResponse> getAvailableReservationTimes(
        List<ReservationTime> reservationTimes) {
        return reservationTimes.stream()
            .map(reservationTime -> ReservationTimeResponse.from(reservationTime, false))
            .toList();
    }

    private List<ReservationTimeResponse> getAvailableReservationTimes(
        Set<ReservationTime> nonDuplicatedReservationTimes) {
        return nonDuplicatedReservationTimes.stream()
            .map(reservationTime -> ReservationTimeResponse.from(reservationTime, true))
            .toList();
    }

    @Transactional
    public ReservationTimeResponse saveTime(ReservationTimeRegister request) {
        validateReservationTime(request);

        ReservationTime savedReservationTime = reservationTimeRepository.save(
            new ReservationTime(request.startAt()));

        return ReservationTimeResponse.from(savedReservationTime);
    }

    private void validateReservationTime(ReservationTimeRegister request) {
        if (reservationTimeRepository.isDuplicatedStartAt(request.startAt())) {
            throw new DuplicatedException("중복된 예약시각은 등록할 수 없습니다.");
        }
    }

    @Transactional
    public void deleteTime(Long id) {
        reservationTimeRepository.deleteById(id);
    }
}
