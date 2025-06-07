package roomescape.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.model.Waiting;

public record WaitingResponse(
    Long id,
    String member,
    String theme,
    LocalDate date,
    LocalTime time) {

    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
            waiting.getId(),
            waiting.getReservation().getMember().getName(),
            waiting.getTheme().getName(),
            waiting.getReservation().getDate(),
            waiting.getReservationTime().getStartAt()
        );
    }
}
