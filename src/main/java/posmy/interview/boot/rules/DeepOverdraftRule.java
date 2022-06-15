package posmy.interview.boot.rules;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

@AllArgsConstructor
@Log4j2
public class DeepOverdraftRule implements Rule {
    BigDecimal overdraft;
    private final BigDecimal charges = new BigDecimal(8);

    @Override
    public boolean evaluate(Expression expression) {
        boolean evalResult = false;
        if (expression.getOperator() == Operator.WITHDRAW
                && expression.getAccount().getBalance().compareTo(BigDecimal.ZERO) < 0) {
            expression.getAccount().withdraw(expression.getAmount(), overdraft);
            expression.getAccount().charge(charges);
            evalResult = true;
            log.info("charges +{}", charges);
        }
        return evalResult;
    }
}