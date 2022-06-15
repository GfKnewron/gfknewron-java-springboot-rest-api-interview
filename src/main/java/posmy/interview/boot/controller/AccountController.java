package posmy.interview.boot.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import posmy.interview.boot.dto.StatementDTO;
import posmy.interview.boot.dto.TransactionDTO;
import posmy.interview.boot.service.AccountService;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = {"/api/v1/account"})
@RolesAllowed("USER")
@Log4j2
public class AccountController {

    private static final String DEPOSIT_LOG = "Deposit user:{} amount:{} balance:{}";
    private static final String WITHDRAW_LOG = "Withdraw user:{} amount:{} balance:{}";


    @Autowired
    @Qualifier("RuleAccountService")
    private AccountService accountService;

    @PostMapping(path = "deposit", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<StatementDTO> deposit(Principal principal, @RequestBody TransactionDTO transactionDTO) {
        Long userId = Long.valueOf(principal.getName());
        BigDecimal total = accountService.deposit(userId, transactionDTO.getAmount());

        log.info(DEPOSIT_LOG, principal.getName(), transactionDTO.getAmount(), total);
        return ResponseEntity.status(HttpStatus.OK).body(new StatementDTO(total));
    }

    @PostMapping(path = "withdraw", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<StatementDTO> withdraw(Principal principal, @RequestBody TransactionDTO transactionDTO) {
        Long userId = Long.valueOf(principal.getName());
        BigDecimal total = accountService.withdraw(userId, transactionDTO.getAmount());

        log.info(WITHDRAW_LOG, principal.getName(), transactionDTO.getAmount(), total);
        return ResponseEntity.status(HttpStatus.OK).body(new StatementDTO(total));
    }
}
