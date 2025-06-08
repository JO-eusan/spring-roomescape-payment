package roomescape.presentation.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.business.service.AdminReservationService;
import roomescape.dto.request.AdminReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.dto.response.WaitingResponse;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 예약 API", description = "관리자용 예약 및 대기 관리 API")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    @Operation(summary = "예약 등록", description = "관리자가 새로운 예약을 등록합니다.")
    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTicketResponse addReservation(
        @RequestBody @Valid AdminReservationRegister request) {
        return adminReservationService.saveReservation(request);
    }

    @Operation(summary = "대기 목록 조회", description = "현재 대기 중인 예약 목록을 조회합니다.")
    @GetMapping("/waitings")
    public List<WaitingResponse> getWaitings() {
        return adminReservationService.getAllWaitings();
    }

    @Operation(summary = "대기 거절", description = "특정 대기 예약을 거절 처리합니다.")
    @DeleteMapping("/waitings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectWaiting(@PathVariable Long id) {
        adminReservationService.rejectWaitingById(id);
    }
}
