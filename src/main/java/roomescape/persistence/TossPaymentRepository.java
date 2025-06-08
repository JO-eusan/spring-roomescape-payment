package roomescape.persistence;

import roomescape.business.model.TossPayment;

public interface TossPaymentRepository {

    TossPayment save(TossPayment tossPayment);
}
