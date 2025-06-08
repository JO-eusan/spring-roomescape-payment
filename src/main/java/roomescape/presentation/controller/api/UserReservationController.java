package roomescape.presentation.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "사용자 예약 API", description = "사용자의 예약 및 대기 정보 조회 API")
public class UserReservationController {

    private final TossPaymentService tossPaymentService;
    private final WaitingService waitingService;

    @Operation(summary = "사용자 예약 및 결제 내역 조회", description = "로그인한 사용자의 예약과 결제 정보를 조회합니다.")
    @GetMapping("/reservations")
    public List<UserReservationPaymentResponse> getUserReservationsWithPayment(
        LoginMember loginMember) {
        return tossPaymentService.getReservationWithPaymentOfMember(loginMember);
    }

    @Operation(summary = "사용자 대기 내역 조회", description = "로그인한 사용자의 대기 정보를 조회합니다.")
    @GetMapping("/waitings")
    public List<UserWaitingResponse> getUserWaitings(LoginMember loginMember) {
        return waitingService.getUserWaitings(loginMember);
    }
}
