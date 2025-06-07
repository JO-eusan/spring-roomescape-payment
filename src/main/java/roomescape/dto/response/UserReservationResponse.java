package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.business.model.ReservationTicket;

public record UserReservationResponse(
    Long id,
    String theme,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
    @JsonFormat(pattern = "HH:mm") LocalTime time) {

    public static UserReservationResponse from(ReservationTicket reservationTicket) {
        return new UserReservationResponse(
            reservationTicket.getId(),
            reservationTicket.getTheme().getName(),
            reservationTicket.getDate(),
            reservationTicket.getReservationTime().getStartAt()
        );
    }
}
