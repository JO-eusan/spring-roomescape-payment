package roomescape.business.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.business.model.Member;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Role;
import roomescape.business.model.Theme;

class ReservationTicketTest {

    @DisplayName("오늘보다 과거로 예약하려고 할 경우 예외처리되도록 한다.")
    @Test
    void test4() {
        // given
        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);

        assertThatThrownBy(() -> new ReservationTicket(new Reservation(
                dateTime.toLocalDate(),
                new ReservationTime(dateTime.toLocalTime()),
                new Theme(1L, "공포", "무서워요", "image")
                , new Member("히로", "example@gmail.com", "password", Role.ADMIN), LocalDate.now().minusDays(1))))
                .isInstanceOf(IllegalStateException.class);
    }
}
