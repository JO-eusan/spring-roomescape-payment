package roomescape.acceptance;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.dto.response.MemberResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MemberAcceptanceTest {

    @Test
    @DisplayName("저장된 멤버 전체를 조회한다")
    void test1() {
        // when
        List<MemberResponse> responseDtos = RestAssured.given().log().all()
            .when().get("/members")
            .then().log().all()
            .statusCode(200).extract()
            .jsonPath().getList(".", MemberResponse.class);

        // then
        assertThat(responseDtos.size()).isEqualTo(2);
    }
}
