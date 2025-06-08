package roomescape.business.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.Member;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.MemberResponse;
import roomescape.dto.response.TokenResponse;
import roomescape.infrastructure.jwt.JwtTokenProvider;
import roomescape.persistence.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public MemberResponse getMemberByToken(String tokenFromCookie) {
        String payload = jwtTokenProvider.getPayload(tokenFromCookie);
        Member member = memberRepository.findByEmail(payload);

        return MemberResponse.from(member);
    }

    public Member getAuthenticatedMember(String tokenFromCookie) {
        String payload = jwtTokenProvider.getPayload(tokenFromCookie);
        return memberRepository.findByEmail(payload);
    }

    @Transactional
    public void login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email());

        if (!member.hasSamePassword(request.password())) {
            throw new UnauthorizedException("로그인에 실패했습니다.");
        }
    }

    @Transactional
    public TokenResponse createToken(String email) {
        String token = jwtTokenProvider.createToken(email, new Date());
        return new TokenResponse(token);
    }
}
