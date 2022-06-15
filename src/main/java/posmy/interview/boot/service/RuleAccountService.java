package posmy.interview.boot.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import posmy.interview.boot.entity.User;
import posmy.interview.boot.exception.FinancialException;
import posmy.interview.boot.repository.UserRepository;
import posmy.interview.boot.rules.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
@Qualifier("RuleAccountService")
public class RuleAccountService implements AccountService {

    public final BigDecimal overdraft = new BigDecimal(50).negate();

    @Autowired
    UserRepository userRepository;

    private static List<Rule> rules = new ArrayList<>();

    @PostConstruct
    public void init() {
        rules.add(new NormalRule(overdraft));
        rules.add(new DeepOverdraftRule(overdraft));
        rules.add(new CrossZeroRule(overdraft));
        rules.add(new DepositRule());
    }

    @Override
    public BigDecimal deposit(Long userId, BigDecimal amount) {
        return alterAmount(userId, amount, Operator.DEPOSIT);
    }

    @Override
    public BigDecimal withdraw(Long userId, BigDecimal sum) {
        return alterAmount(userId, sum, Operator.WITHDRAW);
    }

    public BigDecimal alterAmount(Long userId, BigDecimal amount, Operator operator) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
            process(new Expression(user.getAccount(), amount, operator));
            return userRepository.save(user).getAccount().getBalance();
        } else {
            throw new FinancialException("Amount should be positive value");
        }
    }

    public void process(Expression expression) {
        if (expression.getAccount().isLocked()) {
            throw new FinancialException("Account is locked");
        } else if (rules
                .stream()
                .filter(r -> r.evaluate(expression))
                .findFirst().isEmpty()) {
            throw new FinancialException("Unknown operation");
        }
    }
}