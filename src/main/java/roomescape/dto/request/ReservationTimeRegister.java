package roomescape.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record ReservationTimeRegister(
    @NotNull @JsonFormat(pattern = "HH:mm") LocalTime startAt) {

}
