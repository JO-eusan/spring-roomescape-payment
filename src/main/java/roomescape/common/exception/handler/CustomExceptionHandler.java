package roomescape.common.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.NotFoundException;
import roomescape.common.exception.OperationNotAllowedException;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.PaymentServerException;
import roomescape.common.exception.ResourceInUseException;
import roomescape.common.exception.UnauthorizedException;

@RestControllerAdvice(basePackages = "roomescape")
public class CustomExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(DuplicatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(DuplicatedException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleException(NotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(OperationNotAllowedException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(ResourceInUseException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleException(ResourceInUseException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleException(UnauthorizedException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(PaymentServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handlePaymentServerException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return String.format("결제 승인 중 서버 내부문제가 발생했습니다.(%s)", ex.getMessage());
    }

    @ExceptionHandler(PaymentClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlePaymentClientException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }
}
