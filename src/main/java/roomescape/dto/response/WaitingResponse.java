package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.business.model.Waiting;

public record WaitingResponse(
    Long id,
    String member,
    String theme,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
    @JsonFormat(pattern = "HH:mm") LocalTime time) {

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
