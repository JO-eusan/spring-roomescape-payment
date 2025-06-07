package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.dto.response.MemberResponse;
import roomescape.model.Role;
import roomescape.infrastructure.jwt.JjwtJwtTokenProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthAcceptanceTest {

    @Autowired
    private JjwtJwtTokenProvider jjwtJwtTokenProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("정상적인 토큰을 쿠키에 담아 사용자 정보를 확인하는 경우 정상적으로 반환한다.")
    void test1() {
        // given
        String email = "email@gmail.com";
        String name = "히로";

        String token = jjwtJwtTokenProvider.createToken(email);

        jdbcTemplate.update("INSERT INTO member"
                        + " (name, email,password, role) VALUES (?, ?, ?, ?)"
                , name, email, "password", Role.ADMIN.name());

        // when
        MemberResponse memberResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract().as(MemberResponse.class);

        // then
        assertThat(memberResponse.name()).isEqualTo(name);
    }
}
