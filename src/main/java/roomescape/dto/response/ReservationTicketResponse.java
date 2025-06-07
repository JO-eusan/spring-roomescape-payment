package roomescape.dto.response;

import java.time.LocalDate;
import roomescape.model.ReservationTicket;

public record ReservationTicketResponse(
    Long id,
    MemberResponse member,
    LocalDate date,
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
