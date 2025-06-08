package roomescape.business.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.MemberResponse;
import roomescape.dto.response.TokenResponse;
import roomescape.infrastructure.jwt.JwtTokenProvider;
import roomescape.business.model.Member;
import roomescape.persistence.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public void login(LoginRequest loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.email());

        if (!member.hasSamePassword(loginRequest.password())) {
            throw new UnauthorizedException("로그인에 실패했습니다.");
        }
    }

    public TokenResponse createToken(String email) {
        String token = jwtTokenProvider.createToken(email, new Date());
        return new TokenResponse(token);
    }

    public MemberResponse getMemberByToken(String tokenFromCookie) {
        String payload = jwtTokenProvider.getPayload(tokenFromCookie);
        Member member = memberRepository.findByEmail(payload);

        return MemberResponse.from(member);
    }

    public Member getAuthenticatedMember(String tokenFromCookie) {
        String payload = jwtTokenProvider.getPayload(tokenFromCookie);
        return memberRepository.findByEmail(payload);
    }

}
