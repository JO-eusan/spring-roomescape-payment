package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.business.model.Member;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Role;
import roomescape.business.model.Theme;
import roomescape.dto.response.UserReservationResponse;
import roomescape.infrastructure.db.MemberJpaRepository;
import roomescape.infrastructure.db.ReservationTicketJpaRepository;
import roomescape.infrastructure.db.ReservationTimeJpaRepository;
import roomescape.infrastructure.db.ThemeJpaRepository;
import roomescape.infrastructure.jwt.JwtTokenProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserReservationAcceptanceTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    ThemeJpaRepository themeJpaRepository;

    @Autowired
    ReservationTimeJpaRepository reservationTimeJpaRepository;

    @Autowired
    ReservationTicketJpaRepository reservationTicketJpaRepository;

    @DisplayName("로그인 토큰이 요청되면 로그인된 사용자의 예약 결과가 응답으로 반환된다.")
    @Test
    void test1() {
        //given
        Member member = saveMember(1L);
        ReservationTime reservationTime = saveTime(LocalTime.of(12, 0));
        Theme theme = saveTheme(1L);

        ReservationTicket reservationTicket = saveReservationTicket(reservationTime, theme, member);

        String token = jwtTokenProvider.createToken(member.getEmail(), new Date());

        //when
        List<UserReservationResponse> responses = RestAssured.given().log().all()
            .cookie("token", token)
            .when().get("/users/reservations")
            .then().log().all()
            .statusCode(200).extract()
            .jsonPath().getList(".", UserReservationResponse.class);

        //then
        List<UserReservationResponse> comparedResponse = List.of(
            UserReservationResponse.from(reservationTicket));

        assertAll(
            () -> assertThat(responses).hasSize(1),
            () -> assertThat(responses).isEqualTo(comparedResponse)
        );
    }

    private Member saveMember(Long tmp) {
        Member member = new Member("이름" + tmp, "이메일" + tmp, "비밀번호" + tmp, Role.USER);
        memberJpaRepository.save(member);

        return member;
    }

    private Theme saveTheme(Long tmp) {
        Theme theme = new Theme("이름" + tmp, "설명" + tmp, "썸네일" + tmp);
        themeJpaRepository.save(theme);

        return theme;
    }

    private ReservationTime saveTime(LocalTime reservationTime) {
        ReservationTime time = new ReservationTime(reservationTime);
        reservationTimeJpaRepository.save(time);

        return time;
    }

    private ReservationTicket saveReservationTicket(
        ReservationTime time, Theme theme, Member member) {
        Reservation reservation = new Reservation(
            LocalDate.now().plusDays(1), time, theme, member, LocalDate.now());
        ReservationTicket reservationTicket = new ReservationTicket(reservation);

        return reservationTicketJpaRepository.save(reservationTicket);
    }
}
