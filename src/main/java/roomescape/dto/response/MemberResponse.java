package roomescape.dto.response;

import roomescape.business.model.Member;
import roomescape.business.model.Role;

public record MemberResponse(
    Long id,
    String name,
    String email,
    Role role) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getName(),
            member.getEmail(),
            member.getRole()
        );
    }
}
