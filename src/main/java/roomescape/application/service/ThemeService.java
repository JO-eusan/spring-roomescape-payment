package roomescape.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.ResourceInUseException;
import roomescape.dto.request.ThemeRegister;
import roomescape.dto.response.ThemeResponse;
import roomescape.model.Theme;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ThemeRepository;
import roomescape.persistence.vo.Period;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private static final int POPULAR_DAY_RANGE = 7;
    private static final int POPULAR_THEME_SIZE = 10;

    private final ThemeRepository themeRepository;
    private final ReservationTicketRepository reservationTicketRepository;

    public List<ThemeResponse> getAllThemes() {
        return themeRepository.findAll().stream()
                .map(ThemeResponse::from)
                .collect(Collectors.toList());
    }

    public ThemeResponse saveTheme(ThemeRegister request) {
        validateTheme(request);

        Theme theme = new Theme(request.name(), request.description(), request.thumbnail());
        Theme savedTheme = themeRepository.save(theme);

        return new ThemeResponse(
                savedTheme.getId(),
                savedTheme.getName(),
                savedTheme.getDescription(),
                savedTheme.getThumbnail()
        );
    }

    private void validateTheme(ThemeRegister themeRegister) {
        boolean duplicatedNameExisted = themeRepository.isDuplicatedName(themeRegister.name());
        if (duplicatedNameExisted) {
            throw new DuplicatedException("중복된 테마명은 등록할 수 없습니다.");
        }
    }

    public void deleteTheme(Long id) {
        try {
            themeRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException("삭제하고자 하는 테마에 예약된 정보가 있습니다.");
        }
    }

    public List<ThemeResponse> findPopularThemes(String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        Period period = new Period(parsedDate, parsedDate.minusDays(POPULAR_DAY_RANGE));

        return themeRepository.findPopularThemesInPeriod(period, POPULAR_THEME_SIZE).stream()
                .map(theme -> new ThemeResponse(
                        theme.getId(),
                        theme.getName(),
                        theme.getDescription(),
                        theme.getThumbnail()))
                .toList();
    }
}


