package roomescape.presentation.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.WaitingService;
import roomescape.dto.LoginMember;
import roomescape.dto.request.WaitingRegister;
import roomescape.dto.response.UserWaitingResponse;
import roomescape.dto.response.WaitingResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waiting")
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaitingResponse registerWaiting(LoginMember loginMember,
                                              @RequestBody @Valid WaitingRegister waitingRegister) {
        return waitingService.registerWaiting(loginMember, waitingRegister);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaiting(LoginMember loginMember, @PathVariable Long id) {
        waitingService.deleteWaiting(loginMember, id);
    }

    @GetMapping("/mine")
    @ResponseStatus(HttpStatus.OK)
    public List<UserWaitingResponse> getMyWaitings(LoginMember loginMember) {
        return waitingService.getMyWaitings(loginMember);
    }
}
