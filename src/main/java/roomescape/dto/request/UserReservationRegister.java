package roomescape.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UserReservationRegister(
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
    @NotNull Long timeId,
    @NotNull Long themeId,
    @NotBlank String paymentKey,
    @NotBlank String orderId,
    @NotNull Long amount) {

}
