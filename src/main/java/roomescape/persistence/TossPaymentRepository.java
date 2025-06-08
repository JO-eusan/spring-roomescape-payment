package roomescape.persistence;

import java.util.List;
import roomescape.business.model.TossPayment;

public interface TossPaymentRepository {

    List<TossPayment> findByMemberId(Long memberId);

    TossPayment save(TossPayment tossPayment);
}
