package posmy.interview.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import posmy.interview.boot.dto.StatementDTO;
import posmy.interview.boot.dto.TransactionDTO;
import posmy.interview.boot.entity.Account;
import posmy.interview.boot.entity.User;
import posmy.interview.boot.repository.UserRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Account API test")
@WithMockUser("1")
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void reset() {
        User user = new User();
        user.setAccount(new Account(BigDecimal.ONE));
        userRepository.save(user);
    }

    @Test
    @DisplayName("Make a deposit")
    void testDeposit() throws Exception {
        TransactionDTO transaction = new TransactionDTO(BigDecimal.ONE);
        MvcResult result = mvc.perform(
                post("/api/v1/account/deposit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk()).andReturn();

        StatementDTO statement = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StatementDTO.class);
        assertTrue(BigDecimal.ONE.add(transaction.getAmount()).compareTo(statement.getTotal()) == 0);
    }

    @Test
    @DisplayName("Make a withdraw")
    void testWithdraw() throws Exception {
        TransactionDTO transaction = new TransactionDTO(BigDecimal.ONE);
        MvcResult result = mvc.perform(
                post("/api/v1/account/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk()).andReturn();

        StatementDTO statement = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StatementDTO.class);
        assertTrue(BigDecimal.ONE.subtract(transaction.getAmount()).compareTo(statement.getTotal()) == 0);
    }

    @Test
    @DisplayName("Check a overdtaft limit")
    void testOverdraft() throws Exception {
        TransactionDTO transaction = new TransactionDTO(new BigDecimal(100));
        MvcResult result = mvc.perform(
                post("/api/v1/account/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isPaymentRequired()).andReturn();
    }

    @Test
    @DisplayName("Check statement")
    void testSatement() throws Exception {
        TransactionDTO transaction = new TransactionDTO(BigDecimal.ONE);

        MvcResult result = mvc.perform(
                post("/api/v1/account/deposit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk()).andReturn();

        User user = userRepository.findById(1L).get();

        StatementDTO statement = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StatementDTO.class);
        assertEquals(user.getAccount().getBalance(), statement.getTotal());
    }

    @Test
    @DisplayName("Make a deposits seqentially")
    void testDepositSequential() throws Exception {
        TransactionDTO transaction = new TransactionDTO(BigDecimal.ONE);
        mvc.perform(
                post("/api/v1/account/deposit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk());

        MvcResult result = mvc.perform(
                post("/api/v1/account/deposit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk()).andReturn();

        User user = userRepository.findById(1L).get();

        StatementDTO statement = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StatementDTO.class);
        assertTrue(BigDecimal.ONE
                .add(transaction.getAmount())
                .add(transaction.getAmount())
                .compareTo(statement.getTotal()) == 0);
        assertEquals(statement.getTotal(), user.getAccount().getBalance());
    }

    @Test
    @DisplayName("Whenever I withdraw money that takes my balance to < 0, the system charges me £5.")
    void testCrossZero() throws Exception {
        TransactionDTO transaction = new TransactionDTO(BigDecimal.TEN);
        MvcResult result = mvc.perform(
                post("/api/v1/account/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk()).andReturn();


        StatementDTO statement = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StatementDTO.class);
        assertTrue(new BigDecimal(-4).compareTo(statement.getTotal()) == 0);
    }

    @Test
    @DisplayName("Whenever I withdraw money when my balance is < 0, the system charges me £8.")
    void testDeepOverdtaft() throws Exception {
        TransactionDTO transaction = new TransactionDTO(BigDecimal.TEN);
        mvc.perform(
                post("/api/v1/account/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk());

        MvcResult result = mvc.perform(
                post("/api/v1/account/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk()).andReturn();


        StatementDTO statement = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StatementDTO.class);
        assertTrue(new BigDecimal(-6).compareTo(statement.getTotal()) == 0);
    }

    @Test
    @DisplayName("Account is block after three consecutive events that led to charges.")
    void testLock() throws Exception {
        TransactionDTO transaction = new TransactionDTO(BigDecimal.TEN);
        mvc.perform(
                post("/api/v1/account/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk());

        mvc.perform(
                post("/api/v1/account/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk());

        mvc.perform(
                post("/api/v1/account/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isOk());

        mvc.perform(
                post("/api/v1/account/deposit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transaction))
        ).andExpect(status().isPaymentRequired());
    }
}
