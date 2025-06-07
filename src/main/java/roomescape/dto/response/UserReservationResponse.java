package roomescape.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.model.ReservationTicket;

public record UserReservationResponse(
    Long id,
    String theme,
    LocalDate date,
    LocalTime time) {

    public static UserReservationResponse from(ReservationTicket reservationTicket) {
        return new UserReservationResponse(
            reservationTicket.getId(),
            reservationTicket.getTheme().getName(),
            reservationTicket.getDate(),
            reservationTicket.getReservationTime().getStartAt()
        );
    }
}
