package roomescape.presentation.controller.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.ReservationService;
import roomescape.business.service.WaitingService;
import roomescape.business.vo.LoginMember;
import roomescape.dto.response.UserReservationResponse;
import roomescape.dto.response.UserWaitingResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserReservationController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    @GetMapping("/reservations")
    public List<UserReservationResponse> getUserReservations(LoginMember loginMember) {
        return reservationService.getReservationsOfMember(loginMember);
    }

    @GetMapping("/waitings")
    public List<UserWaitingResponse> getUserWaitings(LoginMember loginMember) {
        return waitingService.getUserWaitings(loginMember);
    }
}
