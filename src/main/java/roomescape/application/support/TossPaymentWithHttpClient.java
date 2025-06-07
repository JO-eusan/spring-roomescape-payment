package roomescape.application.support;

import roomescape.dto.request.TossPaymentConfirm;
import roomescape.dto.response.TossPaymentResponse;

public interface TossPaymentWithHttpClient {

    TossPaymentResponse requestConfirmation(TossPaymentConfirm tossPaymentConfirm);

}
