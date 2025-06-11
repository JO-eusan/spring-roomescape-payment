package roomescape.infrastructure.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.business.model.TossPayment;

public interface TossPaymentJpaRepository extends JpaRepository<TossPayment, Long> {

    List<TossPayment> findByReservationTicket_Reservation_Member_Id(Long memberId);
}
