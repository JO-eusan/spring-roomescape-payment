package roomescape.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestControllerAdvice(basePackages = "roomescape")
public class CustomExceptionHandler {

    private static final String LOGGING_FORMAT = "[EXCEPTION] {} {} - message: {}";

    @ExceptionHandler(DuplicatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(DuplicatedException e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleException(NotFoundException e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(OperationNotAllowedException e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(ResourceInUseException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleException(ResourceInUseException e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleException(UnauthorizedException e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(PaymentServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handlePaymentServerException(Exception e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return String.format("결제 승인 중 서버 내부문제가 발생했습니다.(%s)", e.getMessage());
    }

    @ExceptionHandler(PaymentClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlePaymentClientException(Exception e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return e.getMessage();
    }
}
