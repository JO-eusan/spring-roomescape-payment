package roomescape.presentation.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.AuthService;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.MemberResponse;
import roomescape.dto.response.TokenResponse;
import roomescape.presentation.support.CookieUtils;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인, 로그아웃 및 로그인 상태 체크 API")
public class AuthController {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @Operation(summary = "로그인 상태 체크", description = "쿠키에서 토큰을 가져와 로그인된 회원 정보를 반환합니다.")
    @GetMapping("/login/check")
    public MemberResponse checkLogin(HttpServletRequest httpServletRequest) {
        String tokenFromCookie = cookieUtils.getToken(httpServletRequest);

        return authService.getMemberByToken(tokenFromCookie);
    }

    @Operation(summary = "로그인", description = "회원 이메일과 비밀번호로 로그인 처리 후 토큰을 쿠키에 저장합니다.")
    @PostMapping("/login")
    public void login(
        @RequestBody @Valid LoginRequest request, HttpServletResponse httpServletResponse) {
        authService.login(request);
        TokenResponse tokenResponse = authService.createToken(request.email());

        cookieUtils.setCookieForToken(httpServletResponse, tokenResponse.token());
    }

    @Operation(summary = "로그아웃", description = "쿠키에 저장된 토큰을 만료시켜 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public void logout(
        HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String tokenFromCookie = cookieUtils.getToken(httpServletRequest);

        cookieUtils.setExpiredCookie(httpServletResponse, tokenFromCookie);
    }
}
