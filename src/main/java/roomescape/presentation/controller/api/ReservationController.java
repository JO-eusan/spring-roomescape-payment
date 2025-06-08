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
import roomescape.business.service.ReservationService;
import roomescape.business.service.coordinator.ReservationPaymentCoordinator;
import roomescape.business.vo.LoginMember;
import roomescape.dto.request.ReservationSearch;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "예약 API", description = "예약 조회, 필터 조회, 등록, 삭제 기능을 제공합니다.")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationPaymentCoordinator reservationPaymentCoordinator;

    @Operation(summary = "전체 예약 조회", description = "모든 예약 내역을 조회합니다.")
    @GetMapping
    public List<ReservationTicketResponse> getReservations() {
        return reservationService.getAllReservations();
    }

    @Operation(summary = "예약 필터 조회", description = "조건에 맞는 예약 내역을 필터링하여 조회합니다.")
    @GetMapping("/filter")
    public List<ReservationTicketResponse> getReservationsByFilter(ReservationSearch request) {
        return reservationService.getReservationByFilter(request);
    }

    @Operation(summary = "예약 등록 및 결제", description = "예약 정보를 등록하고 결제를 처리합니다.")
    @PostMapping("/toss")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTicketResponse addReservation(
        @RequestBody @Valid UserReservationRegister request, LoginMember loginMember) {
        return reservationPaymentCoordinator.saveReservationWithPayment(request, loginMember);
    }

    @Operation(summary = "예약 삭제", description = "예약 ID로 해당 예약을 취소합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable("id") Long id) {
        reservationService.cancelReservation(id);
    }
}
