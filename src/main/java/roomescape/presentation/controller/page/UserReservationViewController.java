package roomescape.presentation.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserReservationViewController {

    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    public String myReservationPage() {
        return "reservation-mine";
    }
}
