package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.dto.request.TossPaymentConfirm;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.dto.response.TossPaymentResponse;
import roomescape.infrastructure.payment.TossPaymentRestClient;
import roomescape.infrastructure.jwt.JwtTokenProvider;
import roomescape.business.model.Role;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationTicketAcceptanceTest {

    @MockitoBean
    TossPaymentRestClient tossPaymentRestClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String email;

    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    @BeforeEach
    void setUp() {
        this.email = "email@gmail.com";
        jdbcTemplate.update("INSERT INTO member"
                + " (name, email,password, role) VALUES (?, ?, ?, ?)"
            , "히로", email, "password", Role.ADMIN.name());
    }

    @Test
    @DisplayName("예약 조회 시 저장된 예약 내역을 모두 가져온다")
    void test1() {
        // given
        insertNewReservationWithJdbcTemplate(1L, 1L);

        // when
        List<ReservationTicketResponse> reservations = RestAssured.given().log().all()
            .cookie("token", createToken())
            .when().get("/reservations")
            .then().log().all()
            .statusCode(200).extract()
            .jsonPath().getList(".", ReservationTicketResponse.class);

        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation_ticket",
            Integer.class);

        // then
        assertThat(reservations.size()).isEqualTo(count);
    }

    @Test
    @DisplayName("존재하지 않는 time_id 를 이용해 예약을 등록하고자 하는 경우 404 를 반환한다.")
    void test3() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", tomorrow.toString());
        params.put("timeId", "345");
        params.put("themeId", "1");
        params.put("paymentKey", "paymentKey");
        params.put("orderId", "orderId");
        params.put("amount", "1000");

        // when & then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", createToken())
            .body(params)
            .when().post("/reservations/toss")
            .then().log().all()
            .statusCode(404);
    }

    @Test
    @DisplayName("존재하지 않는 themeId 를 이용해 예약을 등록하고자 하는 경우 404 를 반환한다.")
    void test4() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", tomorrow.toString());
        params.put("timeId", "1");
        params.put("themeId", "123");
        params.put("paymentKey", "paymentKey");
        params.put("orderId", "orderId");
        params.put("amount", "1000");

        // when & then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", createToken())
            .body(params)
            .when().post("/reservations/toss")
            .then().log().all()
            .statusCode(404);
    }

    @Test
    @DisplayName("이미 예약된 테마와 시간에 또 다른 예약을 등록하고자 하는 경우 409 를 반환한다")
    void test5() {
        // given
        Long timeId = 1L;
        Long themeId = 1L;
        insertNewReservationWithJdbcTemplate(timeId, themeId);

        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", tomorrow.toString());
        params.put("timeId", timeId.toString());
        params.put("themeId", themeId.toString());
        params.put("paymentKey", "paymentKey");
        params.put("orderId", "orderId");
        params.put("amount", "1000");

        // when & then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", createToken())
            .body(params)
            .when().post("/reservations/toss")
            .then().log().all()
            .statusCode(409);
    }

    @Test
    @DisplayName("정상적으로 예약이 등록되는 경우 201을 반환한다")
    void test6() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", tomorrow.toString());
        params.put("timeId", "1");
        params.put("themeId", "1");
        params.put("paymentKey", "paymentKey");
        params.put("orderId", "orderId");
        params.put("amount", "1000");

        TossPaymentResponse tossPaymentResponse = new TossPaymentResponse(
            "DONE", "paymentKey", "orderId", 1000L
        );

        when(tossPaymentRestClient.requestConfirmation(any(TossPaymentConfirm.class)))
            .thenReturn(tossPaymentResponse);

        // when & then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", createToken())
            .body(params)
            .when().post("/reservations/toss")
            .then().log().all()
            .statusCode(201);
    }

    @Test
    @DisplayName("특정 예약을 삭제하는 경우 성공 시 204를 반환한다")
    void test7() {
        // given
        Long timeId = 1L;
        Long themeId = 1L;
        Long savedId = insertNewReservationWithJdbcTemplate(timeId, themeId);

        // when & then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", createToken())
            .when().delete("/reservations/" + savedId)
            .then().log().all()
            .statusCode(204);
    }

    private Long insertNewReservationWithJdbcTemplate(final Long timeId, final Long themeId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO reservation_ticket (date, reservation_time_id, theme_id, member_id) VALUES (?, ?, ?, ?)",
                new String[]{"id"});
            ps.setDate(1, Date.valueOf(tomorrow));
            ps.setLong(2, timeId);
            ps.setLong(3, themeId);
            ps.setLong(4, 1L);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    private String createToken() {
        return jwtTokenProvider.createToken(email, new java.util.Date());
    }
}
