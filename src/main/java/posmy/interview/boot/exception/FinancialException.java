package posmy.interview.boot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class FinancialException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FinancialException(String message) {
        super(message);
    }
}