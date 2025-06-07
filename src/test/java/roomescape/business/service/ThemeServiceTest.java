package roomescape.business.service;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.dto.request.ThemeRegister;
import roomescape.dto.response.ThemeResponse;
import roomescape.business.model.Theme;
import roomescape.persistence.repository.MemberRepository;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;
import roomescape.persistence.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ThemeServiceTest {

    @Autowired
    ThemeService themeService;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ReservationTicketRepository reservationTicketRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationTimeRepository reservationTimeRepository;

    @DisplayName("모든 테마를 조회할 수 있다.")
    @Test
    void test1() {

        //when
        List<ThemeResponse> responses = themeService.getAllThemes();

        List<String> actual = responses.stream()
                .map(ThemeResponse::name)
                .toList();

        //then
        assertAll(
                () -> assertThat(actual).hasSize(10),
                () -> assertThat(actual).contains(
                        "공포의 저택",
                        "미스터리 학교",
                        "마법사의 방",
                        "우주선 탈출",
                        "탐정 사무소",
                        "사라진 유물",
                        "지하 감옥",
                        "해적의 보물",
                        "유령 열차",
                        "저주받은 인형"
                )
        );
    }


    @DisplayName("테마를 저장한다.")
    @Test
    void test2() {
        // given
        final String name = "테마테마";
        final String description = "테마입니다";
        final String thumbnail = "image";
        ThemeRegister themeRegister = new ThemeRegister(name, description, thumbnail);

        // when
        ThemeResponse actual = themeService.saveTheme(themeRegister);

        // then
        assertAll(
                () -> assertThat(actual.name()).isEqualTo(name),
                () -> assertThat(actual.description()).isEqualTo(description),
                () -> assertThat(actual.thumbnail()).isEqualTo(thumbnail)
        );
    }

    @DisplayName("테마를 삭제한다")
    @Test
    void test3() {
        // given
        Theme theme = new Theme("테마", "설명", "이미지");
        Theme savedTheme = themeRepository.save(theme);

        // when
        themeService.deleteTheme(savedTheme.getId());

        // then
        List<Theme> themes = themeRepository.findAll();

        List<Long> actual = themes.stream()
                .map(Theme::getId)
                .toList();

        assertThat(actual).doesNotContain(savedTheme.getId());
    }
}
