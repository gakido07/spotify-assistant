package kara.spotifyassistant.exception;

import kara.spotifyassistant.Models.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<Object> handleRuntimeException(HttpClientErrorException exception) {
        return new ResponseEntity<>(
            formatExceptionResponse(Objects.requireNonNull(exception.getMessage())),
            exception.getStatusCode()
        );
    }

    private ExceptionResponse formatExceptionResponse(String message) {
        return new ExceptionResponse(message.substring(4));
    }
}
