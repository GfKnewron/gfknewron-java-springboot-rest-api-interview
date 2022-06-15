package posmy.interview.boot.rules;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DepositRule implements Rule {

    @Override
    public boolean evaluate(Expression expression) {
        boolean evalResult = false;
        if (expression.getOperator() == Operator.DEPOSIT) {
            expression.getAccount().deposit(expression.getAmount());
            evalResult = true;
            log.info("deposit {}", expression.getAmount());
        }
        return evalResult;
    }
}