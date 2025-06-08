package roomescape.persistence;

import java.util.List;
import roomescape.business.model.Member;

public interface MemberRepository {

    List<Member> findAll();

    Member findByEmail(String email);

    Member findById(Long id);
}
