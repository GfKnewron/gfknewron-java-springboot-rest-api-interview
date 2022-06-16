package posmy.interview.boot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import posmy.interview.boot.exception.FinancialException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column
    private int chargesCount = 0;

    @Column
    private boolean isLocked = false;

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void deposit(BigDecimal sum) {
        balance = balance.add(sum);
        chargesCount = 0;
    }

    public void charge(BigDecimal sum) {
        balance = balance.add(sum);
        if (++chargesCount > 2) {
            isLocked = true;
        }
    }

    public void withdraw(BigDecimal sum, BigDecimal overdraft) {
        if (balance.subtract(sum).compareTo(overdraft) < 0) {
            throw new FinancialException("Out of overdraft");
        } else {
            balance = balance.subtract(sum);
        }
    }
}
