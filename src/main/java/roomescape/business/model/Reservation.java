package roomescape.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Reservation {

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false)
    private ReservationTime reservationTime;

    @ManyToOne(optional = false)
    private Theme theme;

    @ManyToOne(optional = false)
    private Member member;

    public Reservation(LocalDate date, ReservationTime reservationTime, Theme theme, Member member,
        LocalDate today) {
        validateReservationDateInFuture(date, today);

        this.date = date;
        this.reservationTime = reservationTime;
        this.theme = theme;
        this.member = member;
    }

    private void validateReservationDateInFuture(LocalDate date, LocalDate today) {
        if (!date.isAfter(today)) {
            throw new IllegalStateException("과거 및 당일 예약은 불가능합니다.");
        }
    }
}
