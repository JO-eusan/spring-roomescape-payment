package roomescape.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.model.Waiting;

public record UserWaitingResponse(
    Long id,
    String theme,
    LocalDate date,
    LocalTime time,
    Long order) {

    public static UserWaitingResponse from(Waiting waiting, Long order) {
        return new UserWaitingResponse(
            waiting.getId(),
            waiting.getTheme().getName(),
            waiting.getReservation().getDate(),
            waiting.getReservationTime().getStartAt(),
            order
        );
    }
}
