package posmy.interview.boot.service;

import java.math.BigDecimal;

public interface AccountService {
    BigDecimal deposit(Long userId, BigDecimal amount);

    BigDecimal withdraw(Long userId, BigDecimal amount);
}
