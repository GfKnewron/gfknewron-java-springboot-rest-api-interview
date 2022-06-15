package posmy.interview.boot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import posmy.interview.boot.exception.FinancialException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private String INCORRECT_OPERATION = "INCORRECT_OPERATION";

    @ExceptionHandler(FinancialException.class)
    public final ResponseEntity<ErrorResponse> handleUserNotFoundException
            (FinancialException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse(INCORRECT_OPERATION, details);
        return new ResponseEntity<>(error, HttpStatus.PAYMENT_REQUIRED);
    }
}
