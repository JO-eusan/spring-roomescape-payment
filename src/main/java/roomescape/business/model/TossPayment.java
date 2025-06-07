package roomescape.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TossPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String status;

    @OneToOne
    @JoinColumn(name = "reservation_ticket_id", nullable = false)
    private ReservationTicket reservationTicket;

    public TossPayment(String paymentKey, String orderId, String status, ReservationTicket reservationTicket) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.status = status;
        this.reservationTicket = reservationTicket;
    }
}
