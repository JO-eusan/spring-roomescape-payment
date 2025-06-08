package roomescape.persistence.implementation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.business.model.Theme;
import roomescape.common.exception.NotFoundException;
import roomescape.infrastructure.db.ThemeJpaRepository;
import roomescape.persistence.ThemeRepository;
import roomescape.business.vo.Period;

@Repository
@RequiredArgsConstructor
public class ThemeRepositoryImpl implements ThemeRepository {

    private final ThemeJpaRepository themeJpaRepository;

    @Override
    public List<Theme> findAll() {
        return themeJpaRepository.findAll();
    }

    @Override
    public List<Theme> findPopularThemesInPeriod(Period period, int size) {
        return themeJpaRepository.findTopReservedThemesSince(period.startDate(), period.endDate(),
            size);
    }

    @Override
    public Theme findById(Long id) {
        return themeJpaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("id 에 해당하는 예약 시각이 존재하지 않습니다."));
    }

    @Override
    public boolean isDuplicatedName(String name) {
        return themeJpaRepository.existsByName(name);
    }

    @Override
    public Theme save(Theme theme) {
        return themeJpaRepository.save(theme);
    }

    @Override
    public void deleteById(Long id) {
        themeJpaRepository.deleteById(id);
    }
}
