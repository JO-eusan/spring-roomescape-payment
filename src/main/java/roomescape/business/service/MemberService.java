package roomescape.business.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.response.MemberResponse;
import roomescape.persistence.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
            .map(MemberResponse::from)
            .toList();
    }
}
