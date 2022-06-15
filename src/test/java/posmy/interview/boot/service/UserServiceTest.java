package posmy.interview.boot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(SimpleUserService.class)
// FIXME explore this
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
class UserServiceTest {

    @Autowired
    AccountService userService;

    @Test
    void notFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.deposit(BigDecimal.ONE));
    }

    @Test
    void deposit1() {
        userService.deposit(BigDecimal.ONE);
    }

    @Test
    void deposit2() {
        userService.deposit(BigDecimal.TEN);
    }
}