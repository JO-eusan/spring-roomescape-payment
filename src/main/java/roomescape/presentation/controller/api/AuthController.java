package roomescape.presentation.controller.api;

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
public class AuthController {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @GetMapping("/login/check")
    public MemberResponse checkLogin(HttpServletRequest httpServletRequest) {
        String tokenFromCookie = cookieUtils.getToken(httpServletRequest);

        return authService.getMemberByToken(tokenFromCookie);
    }

    @PostMapping("/login")
    public void login(
        @RequestBody @Valid LoginRequest request, HttpServletResponse httpServletResponse) {
        authService.login(request);
        TokenResponse tokenResponse = authService.createToken(request.email());

        cookieUtils.setCookieForToken(httpServletResponse, tokenResponse.token());
    }

    @PostMapping("/logout")
    public void logout(
        HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String tokenFromCookie = cookieUtils.getToken(httpServletRequest);

        cookieUtils.setExpiredCookie(httpServletResponse, tokenFromCookie);
    }
}
