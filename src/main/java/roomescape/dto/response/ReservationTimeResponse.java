package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import roomescape.business.model.ReservationTime;

public record ReservationTimeResponse(
    Long id,
    @JsonFormat(pattern = "HH:mm") LocalTime startAt,
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
