package roomescape.business.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.Theme;
import roomescape.business.vo.Period;
import roomescape.common.exception.DuplicatedException;
import roomescape.dto.request.ThemeRegister;
import roomescape.dto.response.ThemeResponse;
import roomescape.persistence.ThemeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ThemeService {

    private static final int POPULAR_DAY_RANGE = 7;
    private static final int POPULAR_THEME_SIZE = 10;

    private final ThemeRepository themeRepository;

    public List<ThemeResponse> getAllThemes() {
        return themeRepository.findAll().stream()
            .map(ThemeResponse::from)
            .toList();
    }

    public List<ThemeResponse> getPopularThemes(String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        Period period = new Period(parsedDate, parsedDate.minusDays(POPULAR_DAY_RANGE));

        return themeRepository.findPopularThemesInPeriod(period, POPULAR_THEME_SIZE).stream()
            .map(theme -> new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail())).toList();
    }

    @Transactional
    public ThemeResponse saveTheme(ThemeRegister request) {
        validateDuplicateTheme(request);
        Theme savedTheme = themeRepository.save(
            new Theme(request.name(), request.description(), request.thumbnail()));

        return ThemeResponse.from(savedTheme);
    }

    private void validateDuplicateTheme(ThemeRegister request) {
        boolean duplicatedNameExisted = themeRepository.isDuplicatedName(request.name());
        if (duplicatedNameExisted) {
            throw new DuplicatedException("중복된 테마명은 등록할 수 없습니다.");
        }
    }

    @Transactional
    public void deleteTheme(Long id) {
        themeRepository.deleteById(id);
    }
}
