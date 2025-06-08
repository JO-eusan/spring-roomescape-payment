package roomescape.business.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.dto.request.ReservationTimeRegister;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.persistence.ReservationTicketRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationTicketTimeServiceTest {

    @Autowired
    ReservationTimeService reservationTimeService;

    @Autowired
    ReservationTicketRepository reservationTicketRepository;

    @Test
    @DisplayName("시간을 삭제한다")
    void test1() {
        // given
        ReservationTimeRegister request = new ReservationTimeRegister(LocalTime.of(15, 0));

        // when
        ReservationTimeResponse response = reservationTimeService.saveTime(request);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.startAt()).isEqualTo(LocalTime.of(15, 0));
    }

    @Test
    @DisplayName("모든 시간을 조회한다")
    void test2() {
        // when
        List<ReservationTimeResponse> times = reservationTimeService.getAllTimes();

        // then
        assertThat(times).hasSize(3);
        assertThat(times).extracting("startAt")
                .containsExactlyInAnyOrder(
                        LocalTime.of(10, 0),
                        LocalTime.of(14, 0),
                        LocalTime.of(18, 0));
    }

    @Test
    @DisplayName("시간을 삭제한다")
    void test3() {
        // given
        ReservationTimeResponse saved = reservationTimeService.saveTime(
                new ReservationTimeRegister(LocalTime.of(15, 0))
        );

        // when
        reservationTimeService.deleteTime(saved.id());

        // then
        List<LocalTime> times = reservationTimeService.getAllTimes().stream()
                .map(ReservationTimeResponse::startAt)
                .toList();

        assertThat(times).doesNotContain(LocalTime.of(15, 0));
    }

}
