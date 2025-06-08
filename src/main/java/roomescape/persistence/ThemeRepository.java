package roomescape.persistence;

import java.util.List;
import roomescape.business.model.Theme;
import roomescape.business.vo.Period;

public interface ThemeRepository {

    List<Theme> findAll();

    List<Theme> findPopularThemesInPeriod(Period period, int size);

    Theme findById(Long id);

    boolean isDuplicatedName(String name);

    Theme save(Theme theme);

    void deleteById(Long id);
}
