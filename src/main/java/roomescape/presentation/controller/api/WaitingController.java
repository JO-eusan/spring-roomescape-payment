package roomescape.presentation.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.WaitingService;
import roomescape.business.vo.LoginMember;
import roomescape.dto.request.WaitingRegister;
import roomescape.dto.response.WaitingResponse;

@RestController
@RequestMapping("/waitings")
@RequiredArgsConstructor
@Tag(name = "예약 대기 API", description = "예약 대기 등록 및 삭제 API")
public class WaitingController {

    private final WaitingService waitingService;

    @Operation(summary = "대기 등록", description = "로그인한 회원이 대기를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaitingResponse registerWaiting(
        @RequestBody @Valid WaitingRegister request, LoginMember loginMember) {
        return waitingService.saveWaiting(request, loginMember);
    }

    @Operation(summary = "대기 삭제", description = "로그인한 회원이 대기를 취소합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaiting(@PathVariable Long id, LoginMember loginMember) {
        waitingService.deleteWaiting(id, loginMember);
    }
}
