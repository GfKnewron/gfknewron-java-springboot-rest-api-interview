package posmy.interview.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import posmy.interview.boot.entity.User;
import posmy.interview.boot.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security test")
class SecurityTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Only authorized are allowed to interact with this system")
    void contextLoads() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "BANK_MANAGER")
    @DisplayName("Role BANK_MANAGER is allowed to /manage/users")
    void testManagerRole() throws Exception {
        given(userRepository.save(any())).willReturn(new User());
        
        mvc.perform(
                post("/api/v1/manage/users/create").with(csrf())
        ).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Role USER not authorized to /create")
    void testUserRole() throws Exception {
        mvc.perform(
                post("/api/v1/manage/users/create").with(csrf())
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("A valid CsrfToken should be populated")
    void testCsrf() throws Exception {
        mvc.perform(
                post("/api/v1/manage/users/create").with(csrf().useInvalidToken())
        ).andExpect(status().isForbidden());
    }
}
