package roomescape.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationSearch(
    @NotNull Long themeId,
    @NotNull Long memberId,
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

}
