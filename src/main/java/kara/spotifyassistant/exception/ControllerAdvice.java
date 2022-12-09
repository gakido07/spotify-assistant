package kara.spotifyassistant.exception;

import kara.spotifyassistant.Models.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<Object> handleHttpException(HttpClientErrorException exception, HttpServletRequest request) {
        return new ResponseEntity<>(
            new ExceptionResponse(
                Objects.requireNonNull(exception.getMessage()).substring(4),
                exception.getStatusCode().value(),
                new Date(),
                request.getRequestURI()
            ),
            exception.getStatusCode()
        );
    }
}
