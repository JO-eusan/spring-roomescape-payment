package roomescape.persistence;

import roomescape.business.model.Member;

public interface MemberRepository {

    Member findByEmail(String email);

    Member findById(Long id);
}
