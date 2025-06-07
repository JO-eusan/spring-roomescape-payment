package roomescape.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AdminReservationRegister(
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
    @NotNull Long themeId,
    @NotNull Long timeId,
    @NotNull Long memberId) {

}
