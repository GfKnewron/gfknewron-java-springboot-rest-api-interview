package posmy.interview.boot.rules;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class NormalRule implements Rule {
    BigDecimal overdraft;

    @Override
    public boolean evaluate(Expression expression) {
        boolean evalResult = false;
        if (expression.getOperator() == Operator.WITHDRAW
                &&
                expression.getAccount().getBalance().compareTo(expression.getAmount()) >= 0) {
            expression.getAccount().withdraw(expression.getAmount(), overdraft);
            evalResult = true;
        }
        return evalResult;
    }
}