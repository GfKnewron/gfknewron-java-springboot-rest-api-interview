package posmy.interview.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.tools.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import posmy.interview.boot.entity.Account;
import posmy.interview.boot.entity.User;
import posmy.interview.boot.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Rest API test")
@WithMockUser(roles = "BANK_MANAGER")
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    public static void initTest() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
                .start();
    }

    @Test
    @DisplayName("User creation")
    void testCreate() throws Exception {
        MvcResult result = mvc.perform(
                post("/api/v1/manage/users/create").with(csrf())
        ).andExpect(status().isCreated()).andExpect(content().string(IsLong.isLong())).andReturn();

        Long userId = Long.valueOf(result.getResponse().getContentAsString());
        assertTrue(userRepository.findById(userId).isPresent(), "User was not created");
    }

    @Test
    @DisplayName("List users")
    void testPageUsers() throws Exception {
        MvcResult result = mvc.perform(
                get("/api/v1/manage/users/page").with(csrf())
        ).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertTrue(StringUtils.isNotBlank(result.getResponse().getContentAsString()), "Users not found");
    }

    @Test
    @DisplayName("Update user")
    void testUpdateUser() throws Exception {
        User user = performUserForTest();
        mvc.perform(
                put("/api/v1/manage/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user))
                        .with(csrf())
        ).andExpect(status().isOk());

        User updated = userRepository.findById(user.getId()).get();
        assertEquals(user.getId(),updated.getId());
        assertEquals(user.getAccount().getId(),updated.getAccount().getId());
        assertEquals(user.getAccount().getAmount(),updated.getAccount().getAmount());
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

    @Test
    @DisplayName("Block user")
    void testBlockUser() throws Exception {
        User user = performUserForTest();
        assertFalse(user.isBlockedFlag());

        mvc.perform(
                post("/api/v1/manage/users/block/" + user.getId())
                        .with(csrf())
        ).andExpect(status().isOk());

        Optional<User> blockedUser = userRepository.findById(user.getId());
        assertTrue(blockedUser.isPresent(), "Blocked user not found");
        assertTrue(blockedUser.get().isBlockedFlag(), "User was not blocked");
    }

    @Test
    @DisplayName("Block user fail")
    void testBlockUserFail() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setAccount(new Account(BigDecimal.ONE));
        userRepository.delete(user);

        mvc.perform(
                post("/api/v1/manage/users/block/" + user.getId())
                        .with(csrf())
        ).andExpect(status().isNotModified());
    }

    @Test
    @DisplayName("Unblock user")
    void testUnblockUser() throws Exception {
        long userId = 1L;

        mvc.perform(
                post("/api/v1/manage/users/block/" + userId)
                        .with(csrf())
        ).andExpect(status().isOk());
    }

    @BeforeEach
    void reset() {
        User user = new User();
        user.setAccount(new Account(BigDecimal.ONE));
        userRepository.save(user);
    }

    private User performUserForTest() {
        return userRepository.findById(1L).get();
    }

}
