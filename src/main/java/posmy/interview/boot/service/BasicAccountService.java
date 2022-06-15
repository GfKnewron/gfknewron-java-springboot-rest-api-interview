package posmy.interview.boot.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import posmy.interview.boot.entity.Account;
import posmy.interview.boot.entity.User;
import posmy.interview.boot.exception.FinancialException;
import posmy.interview.boot.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.function.Consumer;


@Service
@Deprecated
public class BasicAccountService implements AccountService {
    public final BigDecimal overdraft = new BigDecimal(50);
    @Autowired
    UserRepository userRepository;

    @Override
    public BigDecimal deposit(Long userId, BigDecimal sum) {
        return alterAmount(userId, sum, account -> account.deposit(sum));
    }

    @Override
    public BigDecimal withdraw(Long userId, BigDecimal sum) {
        return alterAmount(userId, sum, account -> account.withdraw(sum, overdraft));
    }

    private BigDecimal alterAmount(Long userId, BigDecimal amount, Consumer<Account> function) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
            function.accept(user.getAccount());
            return user.getAccount().getBalance();
        } else {
            throw new FinancialException("Amount should be positive value");
        }
    }
}
