package roomescape.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ThemeRegister(
    @NotBlank String name,
    @NotBlank String description,
    @NotBlank String thumbnail) {

}
