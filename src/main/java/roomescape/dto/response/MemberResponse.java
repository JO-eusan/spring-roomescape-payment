package roomescape.dto.response;

import roomescape.model.Member;
import roomescape.model.Role;

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
