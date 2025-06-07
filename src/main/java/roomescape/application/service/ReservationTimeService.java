package roomescape.application.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.ResourceInUseException;
import roomescape.dto.request.ReservationTimeRegisterDto;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;

@Service
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationTicketRepository reservationTicketRepository;

    public List<ReservationTimeResponse> getAllTimes() {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();

        return reservationTimes.stream().map(ReservationTimeResponse::from).toList();
    }

    public ReservationTimeResponse saveTime(
        ReservationTimeRegisterDto reservationTimeRegisterDto) {
        validateReservationTime(reservationTimeRegisterDto);

        ReservationTime reservationTime = reservationTimeRegisterDto.convertToTime();
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        return ReservationTimeResponse.from(savedReservationTime);
    }

    public void deleteTime(Long id) {
        try {
            reservationTimeRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException("삭제하고자 하는 시각에 예약된 정보가 있습니다.");
        }
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
        return reservationTicketRepository.findForThemeOnDate(themeId, parsedDate);
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

    private void validateReservationTime(ReservationTimeRegisterDto reservationTimeRegisterDto) {
        LocalTime parsedStartAt = LocalTime.parse(reservationTimeRegisterDto.startAt());

        if (reservationTimeRepository.isDuplicatedStartAt((parsedStartAt))) {
            throw new DuplicatedException("중복된 예약시각은 등록할 수 없습니다.");
        }
    }
}



