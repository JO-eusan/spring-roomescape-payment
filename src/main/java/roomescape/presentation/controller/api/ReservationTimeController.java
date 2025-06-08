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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.ReservationTimeService;
import roomescape.dto.request.ReservationTimeRegister;
import roomescape.dto.response.ReservationTimeResponse;

@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
@Tag(name = "예약 시간 API", description = "예약 가능한 시간 조회 및 관리 API")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "전체 예약 시간 조회", description = "모든 예약 시간을 조회합니다.")
    @GetMapping
    public List<ReservationTimeResponse> getTimes() {
        return reservationTimeService.getAllTimes();
    }

    @Operation(summary = "가능한 예약 시간 조회", description = "특정 날짜와 테마 ID에 맞는 가능한 예약 시간을 조회합니다.")
    @GetMapping("/available")
    public List<ReservationTimeResponse> getAvailableTimes(
        @RequestParam String date, @RequestParam Long themeId) {
        return reservationTimeService.getAvailableTimes(date, themeId);
    }

    @Operation(summary = "예약 시간 등록", description = "새로운 예약 시간을 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTimeResponse addTime(@RequestBody @Valid ReservationTimeRegister request) {
        return reservationTimeService.saveTime(request);
    }

    @Operation(summary = "예약 시간 삭제", description = "예약 시간 ID로 예약 시간을 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTime(@PathVariable("id") Long id) {
        reservationTimeService.deleteTime(id);
    }
}
