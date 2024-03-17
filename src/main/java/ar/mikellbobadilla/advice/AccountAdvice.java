package ar.mikellbobadilla.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;
import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
public class AccountAdvice {
    
    @ExceptionHandler(AccountException.class)
    ResponseEntity<ErrorResponse> handleAccountException(AccountException exc) {
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(exc.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }   

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFoundException(AccountNotFoundException exc) {

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(exc.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
