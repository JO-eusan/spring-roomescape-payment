package roomescape.presentation.controller.api;

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
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaitingResponse registerWaiting(
        @RequestBody @Valid WaitingRegister request, LoginMember loginMember) {
        return waitingService.registerWaiting(request, loginMember);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaiting(@PathVariable Long id, LoginMember loginMember) {
        waitingService.deleteWaiting(id, loginMember);
    }
}
