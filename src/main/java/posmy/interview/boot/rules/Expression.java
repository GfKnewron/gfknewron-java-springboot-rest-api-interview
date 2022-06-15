package posmy.interview.boot.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import posmy.interview.boot.entity.Account;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class Expression {
    private Account account;
    private BigDecimal amount;
    private Operator operator;
}