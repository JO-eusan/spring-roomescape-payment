package roomescape.presentation.controller.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.TossPaymentService;
import roomescape.business.service.WaitingService;
import roomescape.business.vo.LoginMember;
import roomescape.dto.response.UserReservationPaymentResponse;
import roomescape.dto.response.UserWaitingResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserReservationController {

    private final TossPaymentService tossPaymentService;
    private final WaitingService waitingService;

    @GetMapping("/reservations")
    public List<UserReservationPaymentResponse> getUserReservationsWithPayment(
        LoginMember loginMember) {
        return tossPaymentService.getReservationWithPaymentOfMember(loginMember);
    }

    @GetMapping("/waitings")
    public List<UserWaitingResponse> getUserWaitings(LoginMember loginMember) {
        return waitingService.getUserWaitings(loginMember);
    }
}
