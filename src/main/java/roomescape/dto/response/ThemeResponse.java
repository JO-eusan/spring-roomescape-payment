package roomescape.dto.response;

import roomescape.business.model.Theme;

public record ThemeResponse(
    Long id,
    String name,
    String description,
    String thumbnail) {

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
            theme.getId(),
            theme.getName(),
            theme.getDescription(),
            theme.getThumbnail()
        );
    }
}
