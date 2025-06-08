package roomescape.persistence.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.business.model.TossPayment;
import roomescape.infrastructure.db.TossPaymentJpaRepository;
import roomescape.persistence.TossPaymentRepository;

@Repository
@RequiredArgsConstructor
public class TossPaymentRepositoryImpl implements TossPaymentRepository {

    private final TossPaymentJpaRepository tossPaymentJpaRepository;

    @Override
    public TossPayment save(TossPayment tossPayment) {
        return tossPaymentJpaRepository.save(tossPayment);
    }
}
