package roomescape.presentation.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.ReservationTicketService;
import roomescape.dto.LoginMember;
import roomescape.dto.response.MemberReservationResponseDto;

@RestController
@RequestMapping("/reservations-mine")
@RequiredArgsConstructor
public class MemberReservationTicketController {

    private final ReservationTicketService reservationTicketService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MemberReservationResponseDto> getMemberReservations(LoginMember loginMember) {
        return reservationTicketService.getReservationsOfMember(loginMember);
    }
}
