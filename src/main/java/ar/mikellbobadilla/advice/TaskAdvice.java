package ar.mikellbobadilla.advice;

import ar.mikellbobadilla.exception.TaskException;
import ar.mikellbobadilla.exception.TaskNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class TaskAdvice {

    @ExceptionHandler(TaskException.class)
    ResponseEntity<ErrorResponse> taskException(TaskException exc) {

        ErrorResponse res = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(exc.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    ResponseEntity<ErrorResponse> taskNotFoundException(TaskNotFoundException exc) {
        ErrorResponse res = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(exc.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }
}
