package roomescape.presentation.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.MemberService;
import roomescape.dto.response.MemberResponse;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 목록 조회 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 목록 조회", description = "모든 회원의 정보를 리스트로 조회합니다.")
    @GetMapping
    public List<MemberResponse> findAll() {
        return memberService.findAll();
    }
}
