package roomescape.presentation.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.ReservationTimeService;
import roomescape.dto.request.ReservationTimeRegister;
import roomescape.dto.response.ReservationTimeResponse;

@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationTimeResponse> getTimes() {
        return reservationTimeService.getAllTimes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTimeResponse addTime(
            @RequestBody @Valid ReservationTimeRegister reservationTimeRegister) {
        return reservationTimeService.saveTime(reservationTimeRegister);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTime(@PathVariable("id") Long id) {
        reservationTimeService.deleteTime(id);
    }

    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationTimeResponse> findAvailableTimes(@RequestParam String date,
                                                                        @RequestParam Long themeId) {
        return reservationTimeService.getAvailableTimes(date, themeId);
    }
}
