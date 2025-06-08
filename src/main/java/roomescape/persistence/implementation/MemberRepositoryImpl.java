package roomescape.persistence.implementation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.business.model.Member;
import roomescape.common.exception.NotFoundException;
import roomescape.infrastructure.db.MemberJpaRepository;
import roomescape.persistence.MemberRepository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public List<Member> findAll() {
        return memberJpaRepository.findAll();
    }

    @Override
    public Member findByEmail(String email) {
        return memberJpaRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    @Override
    public Member findById(Long id) {
        return memberJpaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }
}
