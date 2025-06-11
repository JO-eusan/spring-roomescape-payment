package roomescape.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WaitingRegister(
    @NotNull Long themeId,
    @NotNull Long timeId,
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date) {

}
