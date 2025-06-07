package roomescape.business.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.common.exception.NotFoundException;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dto.request.LoginRequest;
import roomescape.infrastructure.jwt.JwtTokenProvider;
import roomescape.persistence.repository.MemberRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthServiceTest {

    @Autowired
    JwtTokenProvider jjwtJwtTokenProvider;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AuthService authService;

    private String email;
    private String password;

    @BeforeEach
    void setUp() {
        this.email = "example@gmail.com";
        this.password = "password";
    }

    @Test
    @DisplayName("존재하지 않는 이메일에 대하여 로그인 요청을 하는 경우 예외가 발생한다.")
    void test1() {
        // given
        LoginRequest loginRequest = new LoginRequest("invalidEmail@gmail.com", "password");

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("틀린 비밀번호로 로그인 요청을 하는 경우 예외가 발생한다.")
    void test2() {
        // given
        LoginRequest loginRequest = new LoginRequest(this.email, "invalidPassword");

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("존재하는 이메일과 일치하는 비밀번호로 로그인 요청을 하는 경우 예외가 발생하지 않는다.")
    void test3() {
        // given
        LoginRequest loginRequest = new LoginRequest(this.email, this.password);

        // when & then
        Assertions.assertDoesNotThrow(() -> authService.login(loginRequest));
    }
}
