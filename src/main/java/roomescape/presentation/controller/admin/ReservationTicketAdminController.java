package roomescape.presentation.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.ReservationTicketAdminService;
import roomescape.dto.request.AdminReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationTicketAdminController {

    private final ReservationTicketAdminService reservationTicketAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTicketResponse addReservation(@RequestBody @Valid AdminReservationRegister adminReservationRegister) {
        return reservationTicketAdminService.saveReservation(adminReservationRegister);
    }
}
