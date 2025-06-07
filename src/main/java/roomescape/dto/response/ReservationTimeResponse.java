package roomescape.dto.response;

import java.time.LocalTime;
import roomescape.model.ReservationTime;

public record ReservationTimeResponse(
    Long id,
    LocalTime startAt,
    Boolean alreadyBooked) {

    public static ReservationTimeResponse from(ReservationTime reservationTime, Boolean isBooked) {
        return new ReservationTimeResponse(
            reservationTime.getId(),
            reservationTime.getStartAt(),
            isBooked
        );
    }

    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(
            reservationTime.getId(),
            reservationTime.getStartAt(),
            null
        );
    }
}
