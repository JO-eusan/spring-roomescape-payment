package roomescape.business.vo;

import roomescape.business.model.Member;
import roomescape.business.model.Role;

public record LoginMember(
    Long id,
    String name,
    String email,
    Role role) {

    public static LoginMember from(Member member) {
        return new LoginMember(
            member.getId(),
            member.getName(),
            member.getEmail(),
            member.getRole()
        );
    }
}
