package roomescape.presentation.controller.api;

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
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTicketResponse addReservation(
        @RequestBody @Valid AdminReservationRegister request) {
        return adminReservationService.saveReservation(request);
    }

    @GetMapping("/waitings")
    public List<WaitingResponse> getWaitings() {
        return adminReservationService.getAllWaitings();
    }

    @DeleteMapping("/waitings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectWaiting(@PathVariable Long id) {
        adminReservationService.rejectWaitingById(id);
    }
}
