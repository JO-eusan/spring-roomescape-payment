package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.business.model.ReservationTicket;

public record ReservationTicketResponse(
    Long id,
    MemberResponse member,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
    ReservationTimeResponse time,
    ThemeResponse theme) {

    public static ReservationTicketResponse from(ReservationTicket reservationTicket) {
        return new ReservationTicketResponse(
            reservationTicket.getId(),
            MemberResponse.from(reservationTicket.getMember()),
            reservationTicket.getDate(),
            ReservationTimeResponse.from(reservationTicket.getReservationTime()),
            ThemeResponse.from(reservationTicket.getTheme())
        );
    }
}
