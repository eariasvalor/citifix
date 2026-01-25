package com.cityfix.citifix;

import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import com.cityfix.citifix.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AdminUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    @BeforeEach
    void setUp() {
        if (!userRepositoryPort.existsByEmail("admin@cityfix.com")) {
            userRepositoryPort.save(new User((UserId) null, "admin@cityfix.com", "pass", new HashSet<>(Set.of("ROLE_ADMIN"))));
        }

        if (!userRepositoryPort.existsByEmail("citizen@cityfix.com")) {
            userRepositoryPort.save(new User((UserId) null, "citizen@cityfix.com", "pass", new HashSet<>(Set.of("ROLE_USER"))));
        }
    }

    @Test
    @DisplayName("Full Flow: Update user roles in database via API")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUserRolesInDatabase() throws Exception {
        User existingUser = userRepositoryPort.findByEmail("citizen@cityfix.com")
                .orElseThrow(() -> new IllegalStateException("User should have been created in setUp"));

        Long id = existingUser.getId().value();

        String patchRequest = """
        {
            "roles": ["ROLE_ADMIN", "ROLE_USER"]
        }
    """;

        mockMvc.perform(patch("/api/admin/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequest))
                .andExpect(status().isOk());

        User updated = userRepositoryPort.findById(existingUser.getId())
                .orElseThrow(() -> new AssertionError("User not found after update"));

        assertThat(updated.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }
}