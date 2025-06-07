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
import roomescape.business.service.ReservationService;
import roomescape.business.service.coordinator.ReservationPaymentService;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationSearch;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.response.ReservationTicketResponse;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationPaymentService reservationPaymentService;

    @GetMapping
    public List<ReservationTicketResponse> getReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/filter")
    public List<ReservationTicketResponse> getReservationsByFilter(ReservationSearch request) {
        return reservationService.searchReservations(request);
    }

    @PostMapping("/toss")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTicketResponse addReservation(
        @RequestBody @Valid UserReservationRegister request, LoginMember loginMember) {
        return reservationPaymentService.saveReservationWithPayment(request, loginMember);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable("id") Long id) {
        reservationService.cancelReservation(id);
    }
}
