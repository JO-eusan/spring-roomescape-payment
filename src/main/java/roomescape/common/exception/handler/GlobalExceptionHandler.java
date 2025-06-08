package roomescape.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.zip.DataFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice(basePackages = "roomescape")
public class GlobalExceptionHandler {

    private static final String LOGGING_FORMAT = "[EXCEPTION] {} {} - message: {}";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnDefinedException(Exception e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return "서버 내부에서 알 수 없는 문제가 발생했습니다.";
    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleResourceAccessException(Exception e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return "외부 요청 시간을 초과했습니다.";
    }

    @ExceptionHandler({
        IllegalStateException.class,
        DataFormatException.class,
        IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Exception e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException e,
        HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return "연관된 데이터가 존재합니다.";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException e, HttpServletRequest request) {
        log.error(LOGGING_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return e.getMessage();
    }
}
