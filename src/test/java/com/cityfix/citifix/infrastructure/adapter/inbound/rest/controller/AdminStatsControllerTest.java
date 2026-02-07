package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.GetGlobalStatsInputPort;
import com.cityfix.citifix.domain.model.GlobalStats;
import com.cityfix.citifix.infrastructure.config.security.JwtAuthenticationFilter;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import com.cityfix.citifix.infrastructure.config.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminStatsController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class AdminStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetGlobalStatsInputPort getGlobalStatsUseCase;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldAccessStats() throws Exception {
        when(getGlobalStatsUseCase.execute())
                .thenReturn(new GlobalStats(0, Map.of(), Map.of()));

        mockMvc.perform(get("/api/admin/stats/global"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats/global"))
                .andExpect(status().isForbidden());
    }
}