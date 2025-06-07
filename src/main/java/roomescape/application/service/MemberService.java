package roomescape.application.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.dto.response.MemberResponse;
import roomescape.infrastructure.db.MemberJpaRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;

    public List<MemberResponse> findAll() {
        return memberJpaRepository.findAll().stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }
}
