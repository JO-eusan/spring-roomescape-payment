package roomescape.application.support;

import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentResponse;

public interface TossPaymentWithHttpClient {

    TossPaymentResponse requestConfirmation(TossPaymentConfirmDto tossPaymentConfirmDto);

}
