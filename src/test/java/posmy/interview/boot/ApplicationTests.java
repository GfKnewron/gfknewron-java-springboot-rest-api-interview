package posmy.interview.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import posmy.interview.boot.entity.Account;
import posmy.interview.boot.entity.User;
import posmy.interview.boot.repository.UserRepository;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Rest API test")
@WithMockUser(roles = "BANK_MANAGER")
class ApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("User creation")
    void testCreate() throws Exception {
        mvc.perform(
                post("/api/v1/manage/users/create").with(csrf())
        ).andExpect(status().isCreated()).andExpect(content().string(IsLong.isLong()));
    }

    @Test
    @DisplayName("List users")
    void testPageUsers() throws Exception {
        User user = new User();
        user.setAccount(new Account(BigDecimal.TEN));
        userRepository.save(user);

        mvc.perform(
                get("/api/v1/manage/users/page").with(csrf())
        ).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Update user")
    void testUpdateUser() throws Exception {
        User user = new User();
        user.setAccount(new Account(BigDecimal.ONE));
        userRepository.save(user);

        mvc.perform(
                put("/api/v1/manage/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user))
                        .with(csrf())
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update failed")
    void testUpdateFailed() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setAccount(new Account(BigDecimal.ONE));
        userRepository.delete(user);

        mvc.perform(
                put("/api/v1/manage/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user))
                        .with(csrf())
        ).andExpect(status().isNotModified());
    }
}
