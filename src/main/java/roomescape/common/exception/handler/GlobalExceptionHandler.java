package roomescape.common.exception.handler;

import java.util.zip.DataFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice(basePackages = "roomescape")
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnDefinedException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return "서버 내부에서 알 수 없는 문제가 발생했습니다.";
    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleResourceAccessException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return "외부 요청 시간을 초과했습니다.";
    }

    @ExceptionHandler({
        IllegalStateException.class,
        DataFormatException.class,
        IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error(ex.getMessage(), ex);
        return "연관된 데이터가 존재합니다.";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }
}
